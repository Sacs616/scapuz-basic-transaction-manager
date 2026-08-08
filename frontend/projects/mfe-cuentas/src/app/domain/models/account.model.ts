export interface AccountRequest {
  clientId: string;
  accountNumber: string;
  type: string;
  balance: number;
  overdraftLimit?: number;
  currency: string;
  status?: string;
}

export interface AccountResponse {
  id: number;
  clientId: string;
  accountNumber: string;
  type: string;
  status: string;
  balance: number;
  availableBalance: number;
  overdraftLimit: number;
  currency: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface AccountListItem extends AccountResponse {
  clientName: string;
}
