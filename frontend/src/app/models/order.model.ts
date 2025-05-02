import { OrderItem } from './order-item.model';

export interface Order {
  id: number;
  userId: number;
  orderItems: OrderItem[];
  totalAmount: number;
  shippingAddress: string;
  paymentMethod: string;
  status: string;
  createdAt: Date;
  updatedAt?: Date;
  trackingNumber?: string;
}