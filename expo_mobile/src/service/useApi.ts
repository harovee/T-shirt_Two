// hooks/useApi.ts
import { useState } from 'react';
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import { BASE_URL_SERVER } from '@env';

const api = axios.create({
  baseURL: BASE_URL_SERVER + '/api/v1/admin',
  timeout: 10000,
});

export const useApi = <T = any>() => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>(null);

  const request = async (
    method: 'get' | 'post' | 'put' | 'delete',
    url: string,
    payload?: any,
    config?: AxiosRequestConfig
  ): Promise<AxiosResponse<T> | undefined> => {
    setLoading(true);
    setError(null);

    try {
      const response = await api.request<T>({
        method,
        url,
        data: payload,
        ...config,
      });
      setData(response.data);
      return response;
    } catch (err: any) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  return {
    data,
    loading,
    error,
    get: (url: string, config?: AxiosRequestConfig) =>
      request('get', url, undefined, config),
    post: (url: string, payload?: any, config?: AxiosRequestConfig) =>
      request('post', url, payload, config),
    put: (url: string, payload?: any, config?: AxiosRequestConfig) =>
      request('put', url, payload, config),
    del: (url: string, config?: AxiosRequestConfig) =>
      request('delete', url, undefined, config),
  };
};
