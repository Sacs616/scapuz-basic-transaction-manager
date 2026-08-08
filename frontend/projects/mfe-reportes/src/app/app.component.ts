import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { ReportFacade } from './application/facades/report.facade';
import { AccountResponse, ReportRow } from './domain/models/report.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private readonly facade = inject(ReportFacade);
  private readonly destroy$ = new Subject<void>();

  reportRows: ReportRow[] = [];
  accounts: AccountResponse[] = [];
  fechaInicio = '';
  fechaFin = '';
  selectedAccountId: number | null = null;
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.facade.rows$.pipe(takeUntil(this.destroy$)).subscribe((rows) => {
      this.reportRows = rows;
    });
    this.facade.accounts$.pipe(takeUntil(this.destroy$)).subscribe((accounts) => {
      this.accounts = accounts;
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

  onSearch(): void {
    this.facade.loadReport(this.fechaInicio, this.fechaFin, this.selectedAccountId);
  }

  onAccountChange(value: string): void {
    this.selectedAccountId = value ? Number(value) : null;
  }

  generatePDF(): void {
    if (this.reportRows.length === 0) {
      alert('No hay datos para exportar. Use Buscar primero.');
      return;
    }
    const doc = new jsPDF();
    const datePipe = new DatePipe('en-US');
    const currencyPipe = new CurrencyPipe('en-US');

    doc.text('Reporte de Movimientos', 14, 15);

    const tableData = this.reportRows.map((r) => [
      datePipe.transform(r.createdAt, 'dd/MM/yyyy') || '',
      r.clientName,
      r.accountNumber,
      r.type,
      currencyPipe.transform(r.balance, r.currency) || '',
      r.status,
      currencyPipe.transform(r.amount, r.currency) || '',
      currencyPipe.transform(r.availableBalance, r.currency) || ''
    ]);

    autoTable(doc, {
      head: [['Fecha', 'Cliente', 'Numero Cuenta', 'Tipo', 'Saldo', 'Estado', 'Movimiento', 'Saldo Disponible']],
      body: tableData,
      startY: 20
    });

    doc.save('reporte_movimientos.pdf');
  }

  dismissError(): void {
    this.facade.clearError();
  }
}
