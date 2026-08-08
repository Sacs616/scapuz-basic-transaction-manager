import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientRequest, ClientResponse } from '../../domain/models/client.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClientApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.clientApiUrl;

  getAll(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.baseUrl);
  }

  getById(id: string): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: ClientRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(this.baseUrl, request);
  }

  update(id: string, request: ClientRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
