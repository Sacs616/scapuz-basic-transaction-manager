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
}
