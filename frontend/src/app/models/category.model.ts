export interface Category {
  id: number;
  name: string;
  description: string;
  parentId?: number;
  parentName?: string;
  imageUrl?: string;
}
