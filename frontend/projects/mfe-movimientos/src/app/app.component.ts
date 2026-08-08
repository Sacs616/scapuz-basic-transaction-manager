import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { TransactionFacade } from './application/facades/transaction.facade';
import { AccountResponse } from './domain/models/account.model';
import { TransactionListItem, TransactionRequest } from './domain/models/transaction.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly facade = inject(TransactionFacade);
  private readonly destroy$ = new Subject<void>();

  transactions: TransactionListItem[] = [];
  filteredTransactions: TransactionListItem[] = [];
  accounts: AccountResponse[] = [];
  selectedAccountId: number | null = null;
  searchTerm = '';
  loading = false;
  error: string | null = null;

  showModal = false;
  movimientoForm: FormGroup;

  constructor() {
    this.movimientoForm = this.fb.group({
      accountId: ['', Validators.required],
      type: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      currency: ['USD', Validators.required],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.facade.transactions$.pipe(takeUntil(this.destroy$)).subscribe((transactions) => {
      this.transactions = transactions;
      this.applySearch();
    });
    this.facade.accounts$.pipe(takeUntil(this.destroy$)).subscribe((accounts) => {
      this.accounts = accounts;
    });
    this.facade.selectedAccountId$.pipe(takeUntil(this.destroy$)).subscribe((id) => {
      this.selectedAccountId = id;
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

  onAccountFilterChange(accountId: string): void {
    const id = accountId ? Number(accountId) : null;
    this.facade.selectAccount(id);
  }

  onSearch(searchTerm: string): void {
    this.searchTerm = searchTerm.toLowerCase();
    this.applySearch();
  }

  private applySearch(): void {
    if (!this.searchTerm) {
      this.filteredTransactions = [...this.transactions];
      return;
    }
    this.filteredTransactions = this.transactions.filter(
      (m) =>
        m.clientName.toLowerCase().includes(this.searchTerm) ||
        m.accountNumber.includes(this.searchTerm)
    );
  }

  openModal(): void {
    this.showModal = true;
    this.facade.clearError();
    const accountId = this.selectedAccountId ?? this.accounts[0]?.id ?? '';
    this.movimientoForm.reset({
      accountId,
      currency: 'USD'
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.movimientoForm.reset({ currency: 'USD' });
  }

  onSubmit(): void {
    if (!this.movimientoForm.valid) {
      this.movimientoForm.markAllAsTouched();
      return;
    }
    const formValue = this.movimientoForm.value;
    const request: TransactionRequest = {
      accountId: Number(formValue.accountId),
      transactionNumber: this.generateTransactionNumber(),
      type: formValue.type,
      amount: Number(formValue.amount),
      currency: formValue.currency,
      description: formValue.description || undefined
    };
    this.facade.createTransaction(request);
    this.closeModal();
  }

  private generateTransactionNumber(): string {
    const suffix = Date.now().toString().slice(-12).padStart(12, '0');
    return `TXN${suffix}`;
  }

  signedAmount(tx: TransactionListItem): number {
    return tx.type === 'WITHDRAWAL' ? -Number(tx.amount) : Number(tx.amount);
  }

  dismissError(): void {
    this.facade.clearError();
  }
}
