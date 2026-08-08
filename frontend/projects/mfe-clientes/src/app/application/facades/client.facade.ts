import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, finalize } from 'rxjs';
import { ClientApiService } from '../../infrastructure/services/client-api.service';
import { ClientRequest, ClientResponse } from '../../domain/models/client.model';

@Injectable({ providedIn: 'root' })
export class ClientFacade {
  private readonly clientApi = inject(ClientApiService);

  private readonly clientsSubject = new BehaviorSubject<ClientResponse[]>([]);
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);

  readonly clients$ = this.clientsSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();

  loadClients(): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.clientApi
      .getAll()
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: (clients) => this.clientsSubject.next(clients),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al cargar clientes')
      });
  }

  createClient(request: ClientRequest): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.clientApi
      .create(request)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: () => this.loadClients(),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al crear cliente')
      });
  }

  updateClient(id: string, request: ClientRequest): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.clientApi
      .update(id, request)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: () => this.loadClients(),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al actualizar cliente')
      });
  }

  deleteClient(id: string): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    this.clientApi
      .delete(id)
      .pipe(finalize(() => this.loadingSubject.next(false)))
      .subscribe({
        next: () => this.loadClients(),
        error: (err) =>
          this.errorSubject.next(err?.error?.message ?? 'Error al eliminar cliente')
      });
  }

  clearError(): void {
    this.errorSubject.next(null);
  }
}
