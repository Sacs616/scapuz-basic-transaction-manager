export interface ClientRequest {
  identification: string;
  name: string;
  genre: string;
  birthDate: string;
  address: string;
  phone: string;
  status?: string;
}

export interface ClientResponse {
  clientId: string;
  identification: string;
  name: string;
  genre: string;
  age?: number;
  address: string;
  phone: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}
