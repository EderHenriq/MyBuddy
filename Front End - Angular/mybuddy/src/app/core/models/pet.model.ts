export interface Pet {
  id: string;
  ownerId: string;
  name: string;
  species: string;
  gender?: string;
  breed?: string;
  age?: number;
  weight?: number;
  createdAt?: string;
  updatedAt?: string;
  imageUrl?: string;
  isVaccinated?: boolean;
}
