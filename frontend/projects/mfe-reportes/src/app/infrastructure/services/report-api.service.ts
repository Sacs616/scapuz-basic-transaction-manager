import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountResponse, TransactionResponse } from '../../domain/models/report.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReportApiService {
  private readonly http = inject(HttpClient);

  getTransactionsByDateRange(startDate: string, endDate: string): Observable<TransactionResponse[]> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<TransactionResponse[]>(`${environment.transactionApiUrl}/date-range`, { params });
  }

  getTransactionsByAccountAndDateRange(
    accountId: number,
    startDate: string,
    endDate: string
  ): Observable<TransactionResponse[]> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<TransactionResponse[]>(
      `${environment.transactionApiUrl}/account/${accountId}/date-range`,
      { params }
    );
  }

  getAllAccounts(): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(environment.accountApiUrl);
  }

  getAllClients(): Observable<{ clientId: string; name: string }[]> {
    return this.http.get<{ clientId: string; name: string }[]>(environment.clientApiUrl);
  }
}
