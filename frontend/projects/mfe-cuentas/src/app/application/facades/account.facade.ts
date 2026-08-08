import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, finalize, forkJoin } from 'rxjs';
import { AccountApiService } from '../../infrastructure/services/account-api.service';
import { ClientApiService } from '../../infrastructure/services/client-api.service';
import { AccountListItem, AccountRequest } from '../../domain/models/account.model';
import { ClientResponse } from '../../domain/models/client.model';

@Injectable({ providedIn: 'root' })
export class AccountFacade {
  private readonly accountApi = inject(AccountApiService);
  private readonly clientApi = inject(ClientApiService);

  private readonly accountsSubject = new BehaviorSubject<AccountListItem[]>([]);
  private readonly clientsSubject = new BehaviorSubject<ClientResponse[]>([]);
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);

  readonly accounts$ = this.accountsSubject.asObservable();
  readonly clients$ = this.clientsSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();

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
          this.clientsSubject.next(clients);
          const clientMap = new Map(clients.map((c) => [c.clientId, c.name]));
          const items: AccountListItem[] = accounts.map((account) => ({
            ...account,
            clientName: clientMap.get(account.clientId) ?? account.clientId
          }));
          this.accountsSubject.next(items);
        },
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al cargar cuentas')
      });
  }

  createAccount(request: AccountRequest): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.accountApi
      .create(request)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: () => this.loadAccounts(),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al crear cuenta')
      });
  }

  updateAccount(id: number, request: AccountRequest): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.accountApi
      .update(id, request)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: () => this.loadAccounts(),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al actualizar cuenta')
      });
  }

  clearError(): void {
    this.errorSubject.next(null);
  }
}
