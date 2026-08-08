import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientResponse } from '../../domain/models/client.model';
import { environment } from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ClientApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.clientApiUrl;

  getAll(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.baseUrl);
  }
}
