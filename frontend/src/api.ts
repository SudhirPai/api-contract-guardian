import axios from 'axios';

export const api = axios.create({ baseURL: '/api' });
export type MonitoredApi = {
    id: number; name: string;
    application: string;
    team: string;
    environment: string;
    swaggerUrl: string;
    description?: string;
    pollingEnabled: boolean;
    currentContractId?: number;
    updatedAt: string
};

export type Change = {
    id: number;
    changeType: string;
    severity: string;
    endpoint: string;
    jsonPath: string;
    oldValue?: string;
    newValue?: string;
    confidence: number;
    detectedAt: string
};

export type Impact = {
    id: number;
    contractChangeId: number;
    mappingId: number;
    impactLevel: string;
    riskScore: number;
    reason: string;
    status: string;
    detectedAt: string
};

