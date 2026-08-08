import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { AccountFacade } from './application/facades/account.facade';
import { AccountListItem, AccountRequest } from './domain/models/account.model';
import { ClientResponse } from './domain/models/client.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly facade = inject(AccountFacade);
  private readonly destroy$ = new Subject<void>();

  accounts: AccountListItem[] = [];
  filteredAccounts: AccountListItem[] = [];
  clients: ClientResponse[] = [];
  searchTerm = '';
  loading = false;
  error: string | null = null;

  showModal = false;
  isEditing = false;
  cuentaForm: FormGroup;
  currentAccountId: number | null = null;

  constructor() {
    this.cuentaForm = this.fb.group({
      clientId: ['', Validators.required],
      accountNumber: ['', Validators.required],
      type: ['', Validators.required],
      balance: [0, [Validators.required, Validators.min(0)]],
      overdraftLimit: [0, [Validators.min(0)]],
      currency: ['USD', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  ngOnInit(): void {
    this.facade.accounts$.pipe(takeUntil(this.destroy$)).subscribe((accounts) => {
      this.accounts = accounts;
      this.applySearch();
    });
    this.facade.clients$.pipe(takeUntil(this.destroy$)).subscribe((clients) => {
      this.clients = clients;
    });
    this.facade.loading$.pipe(takeUntil(this.destroy$)).subscribe((loading) => {
      this.loading = loading;
    });
    this.facade.error$.pipe(takeUntil(this.destroy$)).subscribe((error) => {
      this.error = error;
    });
    this.facade.loadAccounts();
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
      this.filteredAccounts = [...this.accounts];
      return;
    }
    this.filteredAccounts = this.accounts.filter(
      (a) =>
        a.accountNumber.toLowerCase().includes(this.searchTerm) ||
        a.clientName.toLowerCase().includes(this.searchTerm)
    );
  }

  openModal(account?: AccountListItem): void {
    this.showModal = true;
    this.facade.clearError();
    if (account) {
      this.isEditing = true;
      this.currentAccountId = account.id;
      this.cuentaForm.patchValue({
        clientId: account.clientId,
        accountNumber: account.accountNumber,
        type: account.type,
        balance: account.balance,
        overdraftLimit: account.overdraftLimit ?? 0,
        currency: account.currency,
        status: account.status
      });
    } else {
      this.isEditing = false;
      this.currentAccountId = null;
      this.cuentaForm.reset({
        balance: 0,
        overdraftLimit: 0,
        currency: 'USD',
        status: 'ACTIVE'
      });
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.cuentaForm.reset({
      balance: 0,
      overdraftLimit: 0,
      currency: 'USD',
      status: 'ACTIVE'
    });
  }

  onSubmit(): void {
    if (!this.cuentaForm.valid) {
      this.cuentaForm.markAllAsTouched();
      return;
    }
    const formValue = this.cuentaForm.value as AccountRequest;
    if (this.isEditing && this.currentAccountId !== null) {
      this.facade.updateAccount(this.currentAccountId, formValue);
    } else {
      this.facade.createAccount(formValue);
    }
    this.closeModal();
  }

  dismissError(): void {
    this.facade.clearError();
  }
}
