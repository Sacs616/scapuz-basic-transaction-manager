import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionRequest, TransactionResponse } from '../../domain/models/transaction.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class TransactionApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.transactionApiUrl;

  getByAccountId(accountId: number): Observable<TransactionResponse[]> {
    return this.http.get<TransactionResponse[]>(`${this.baseUrl}/account/${accountId}`);
  }

  getByDateRange(startDate: string, endDate: string): Observable<TransactionResponse[]> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<TransactionResponse[]>(`${this.baseUrl}/date-range`, { params });
  }

  create(request: TransactionRequest): Observable<TransactionResponse> {
    return this.http.post<TransactionResponse>(this.baseUrl, request);
  }
}
