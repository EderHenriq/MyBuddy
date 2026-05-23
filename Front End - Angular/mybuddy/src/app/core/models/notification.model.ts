export interface AppNotification {
  id: string;
  title: string;
  message: string;
  date: string;
  read: boolean;
  type: 'info' | 'success' | 'warning' | 'error';
  link?: string;
}

export interface ActivityHistory {
  id: string;
  action: string;
  description: string;
  date: string;
  icon: string;
  type: 'auth' | 'pet' | 'system' | 'adoption';
}
