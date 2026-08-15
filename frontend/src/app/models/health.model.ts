export interface HealthResponse {
  status: string;
  application: string;
  message: string;
}

export interface DatabaseHealthResponse {
  status: string;
  database: string;
  message: string;
}

export interface SystemHealthState {
  backend: {
    loading: boolean;
    data: HealthResponse | null;
    error: string | null;
    checkedAt: Date | null;
  };
  database: {
    loading: boolean;
    data: DatabaseHealthResponse | null;
    error: string | null;
    checkedAt: Date | null;
  };
}
