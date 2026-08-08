import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, finalize, forkJoin, of } from 'rxjs';
import { ReportApiService } from '../../infrastructure/services/report-api.service';
import { AccountResponse, ReportRow, TransactionResponse } from '../../domain/models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportFacade {
  private readonly reportApi = inject(ReportApiService);

  private readonly rowsSubject = new BehaviorSubject<ReportRow[]>([]);
  private readonly accountsSubject = new BehaviorSubject<AccountResponse[]>([]);
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);

  readonly rows$ = this.rowsSubject.asObservable();
  readonly accounts$ = this.accountsSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();

  private accountsCache: AccountResponse[] = [];
  private clientNameById = new Map<string, string>();

  loadAccounts(): void {
    this.reportApi.getAllAccounts().subscribe({
      next: (accounts) => {
        this.accountsCache = accounts;
        this.accountsSubject.next(accounts);
      },
      error: (err) =>
        this.errorSubject.next(err?.error?.message ?? 'Error al cargar cuentas')
    });
  }

  loadReport(startDate: string, endDate: string, accountId: number | null): void {
    if (!startDate || !endDate) {
      this.errorSubject.next('Seleccione fecha inicio y fin');
      return;
    }
    const startIso = `${startDate}T00:00:00`;
    const endIso = `${endDate}T23:59:59`;

    this.loadingSubject.next(true);
    this.errorSubject.next(null);

    const transactions$ =
      accountId !== null
        ? this.reportApi.getTransactionsByAccountAndDateRange(accountId, startIso, endIso)
        : this.reportApi.getTransactionsByDateRange(startIso, endIso);

    forkJoin({
      transactions: transactions$,
      clients: this.reportApi.getAllClients(),
      accounts: this.accountsCache.length
        ? of(this.accountsCache)
        : this.reportApi.getAllAccounts()
    })
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: ({ transactions, clients, accounts }) => {
          this.accountsCache = accounts;
          this.accountsSubject.next(accounts);
          this.clientNameById = new Map(clients.map((c) => [c.clientId, c.name]));
          this.rowsSubject.next(this.toReportRows(transactions, accounts));
        },
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al cargar reporte')
      });
  }

  private toReportRows(transactions: TransactionResponse[], accounts: AccountResponse[]): ReportRow[] {
    const accountMap = new Map(accounts.map((a) => [a.id, a]));
    return transactions.map((tx) => {
      const account = accountMap.get(tx.accountId);
      const clientName = account ? (this.clientNameById.get(account.clientId) ?? '') : '';
      const signedAmount = tx.type === 'WITHDRAWAL' ? -Number(tx.amount) : Number(tx.amount);
      return {
        createdAt: tx.createdAt,
        clientName,
        accountNumber: account?.accountNumber ?? '',
        type: tx.type,
        balance: account?.balance ?? 0,
        status: tx.status,
        amount: signedAmount,
        availableBalance: account?.availableBalance ?? 0,
        currency: tx.currency
      };
    });
  }

  clearError(): void {
    this.errorSubject.next(null);
  }
}
