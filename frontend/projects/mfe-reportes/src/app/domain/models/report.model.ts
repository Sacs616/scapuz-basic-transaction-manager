export interface TransactionResponse {
  id: number;
  accountId: number;
  transactionNumber: string;
  type: string;
  amount: number;
  currency: string;
  status: string;
  description?: string;
  referenceNumber?: string;
  createdAt: string;
  processedAt?: string;
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
}

export interface ReportRow {
  createdAt: string;
  clientName: string;
  accountNumber: string;
  type: string;
  balance: number;
  status: string;
  amount: number;
  availableBalance: number;
  currency: string;
}
