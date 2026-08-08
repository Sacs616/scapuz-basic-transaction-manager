import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ClientFacade } from './application/facades/client.facade';
import { ClientRequest, ClientResponse } from './domain/models/client.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly facade = inject(ClientFacade);
  private readonly destroy$ = new Subject<void>();

  clients: ClientResponse[] = [];
  filteredClients: ClientResponse[] = [];
  searchTerm = '';
  loading = false;
  error: string | null = null;

  showModal = false;
  isEditing = false;
  clienteForm: FormGroup;
  currentClientId: string | null = null;

  constructor() {
    this.clienteForm = this.fb.group({
      name: ['', Validators.required],
      identification: ['', Validators.required],
      birthDate: ['', Validators.required],
      genre: ['', Validators.required],
      address: ['', Validators.required],
      phone: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  ngOnInit(): void {
    this.facade.clients$.pipe(takeUntil(this.destroy$)).subscribe((clients) => {
      this.clients = clients;
      this.applySearch();
    });
    this.facade.loading$.pipe(takeUntil(this.destroy$)).subscribe((loading) => {
      this.loading = loading;
    });
    this.facade.error$.pipe(takeUntil(this.destroy$)).subscribe((error) => {
      this.error = error;
    });
    this.facade.loadClients();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearch(searchTerm: string): void {
    this.searchTerm = searchTerm.toLowerCase();
    this.applySearch();
  }

  private applySearch(): void {
    if (!this.searchTerm) {
      this.filteredClients = [...this.clients];
      return;
    }
    this.filteredClients = this.clients.filter(
      (c) =>
        c.name.toLowerCase().includes(this.searchTerm) ||
        c.identification.includes(this.searchTerm)
    );
  }

  openModal(client?: ClientResponse): void {
    this.showModal = true;
    this.facade.clearError();
    if (client) {
      this.isEditing = true;
      this.currentClientId = client.clientId;
      this.clienteForm.patchValue({
        name: client.name,
        identification: client.identification,
        birthDate: '',
        genre: client.genre,
        address: client.address,
        phone: client.phone,
        status: client.status
      });
    } else {
      this.isEditing = false;
      this.currentClientId = null;
      this.clienteForm.reset({ status: 'ACTIVE' });
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.clienteForm.reset({ status: 'ACTIVE' });
  }

  onSubmit(): void {
    if (!this.clienteForm.valid) {
      this.clienteForm.markAllAsTouched();
      return;
    }
    const formValue = this.clienteForm.value as ClientRequest;
    if (this.isEditing && this.currentClientId) {
      this.facade.updateClient(this.currentClientId, formValue);
    } else {
      this.facade.createClient(formValue);
    }
    this.closeModal();
  }

  deleteClient(id: string): void {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
      this.facade.deleteClient(id);
    }
  }

  dismissError(): void {
    this.facade.clearError();
  }
}
