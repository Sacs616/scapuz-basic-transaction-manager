import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, finalize, forkJoin, switchMap } from 'rxjs';
import { TransactionApiService } from '../../infrastructure/services/transaction-api.service';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { ClientApiService } from '../../infrastructure/services/client-api.service';
import { TransactionListItem, TransactionRequest, TransactionResponse } from '../../domain/models/transaction.model';
import { AccountResponse } from '../../domain/models/account.model';
import { ClientResponse } from '../../domain/models/client.model';

@Injectable({ providedIn: 'root' })
export class TransactionFacade {
  private readonly transactionApi = inject(TransactionApiService);
  private readonly accountApi = inject(AccountApiService);
  private readonly clientApi = inject(ClientApiService);

  private readonly transactionsSubject = new BehaviorSubject<TransactionListItem[]>([]);
  private readonly accountsSubject = new BehaviorSubject<AccountResponse[]>([]);
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);
  private readonly selectedAccountIdSubject = new BehaviorSubject<number | null>(null);

  readonly transactions$ = this.transactionsSubject.asObservable();
  readonly accounts$ = this.accountsSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();
  readonly selectedAccountId$ = this.selectedAccountIdSubject.asObservable();

  private accountsCache: AccountResponse[] = [];
  private clientsCache: ClientResponse[] = [];

  loadAccounts(): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    forkJoin({
      accounts: this.accountApi.getAll(),
      clients: this.clientApi.getAll()
    })
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: ({ accounts, clients }) => {
          this.accountsCache = accounts;
          this.clientsCache = clients;
          this.accountsSubject.next(accounts);
          if (accounts.length > 0 && this.selectedAccountIdSubject.value === null) {
            this.selectAccount(accounts[0].id);
          }
        },
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al cargar cuentas')
      });
  }

  selectAccount(accountId: number | null): void {
    this.selectedAccountIdSubject.next(accountId);
    if (accountId === null) {
      this.transactionsSubject.next([]);
      return;
    }
    this.loadTransactionsForAccount(accountId);
  }

  loadTransactionsForAccount(accountId: number): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.transactionApi
      .getByAccountId(accountId)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: (transactions) =>
          this.transactionsSubject.next(this.enrichTransactions(transactions)),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al cargar movimientos')
      });
  }

  createTransaction(request: TransactionRequest): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.transactionApi
      .create(request)
      .pipe(
        switchMap(() => this.transactionApi.getByAccountId(request.accountId)),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe({
        next: (transactions) => {
          this.selectedAccountIdSubject.next(request.accountId);
          this.transactionsSubject.next(this.enrichTransactions(transactions));
        },
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al crear movimiento')
      });
  }

  private enrichTransactions(transactions: TransactionResponse[]): TransactionListItem[] {
    const accountMap = new Map(this.accountsCache.map((a) => [a.id, a]));
    const clientMap = new Map(this.clientsCache.map((c) => [c.clientId, c.name]));
    return transactions.map((tx) => {
      const account = accountMap.get(tx.accountId);
      const clientName = account ? (clientMap.get(account.clientId) ?? '') : '';
      return {
        ...tx,
        accountNumber: account?.accountNumber ?? '',
        clientName
      };
    });
  }

  clearError(): void {
    this.errorSubject.next(null);
  }
}
