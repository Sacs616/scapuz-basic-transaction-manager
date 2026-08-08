export interface TransactionRequest {
  accountId: number;
  transactionNumber: string;
  type: string;
  amount: number;
  currency: string;
  description?: string;
  referenceNumber?: string;
}

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

export interface TransactionListItem extends TransactionResponse {
  accountNumber: string;
  clientName: string;
}
