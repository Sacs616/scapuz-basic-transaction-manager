import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountRequest, AccountResponse } from '../../domain/models/account.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class AccountApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.accountApiUrl;

  getAll(): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<AccountResponse> {
    return this.http.get<AccountResponse>(`${this.baseUrl}/${id}`);
  }

  getByClientId(clientId: string): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>(`${this.baseUrl}/client/${clientId}`);
  }

  create(request: AccountRequest): Observable<AccountResponse> {
    return this.http.post<AccountResponse>(this.baseUrl, request);
  }

  update(id: number, request: AccountRequest): Observable<AccountResponse> {
    return this.http.put<AccountResponse>(`${this.baseUrl}/${id}`, request);
  }
}
