// Centralized API Client for AI Recruitment Platform
// Routes all requests through API Gateway (http://localhost:8080) with JWT and API Key fallback

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export async function request(endpoint, options = {}) {
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;

  // Retrieve JWT token from localStorage if available
  const token = localStorage.getItem('auth_token');
  const user = JSON.parse(localStorage.getItem('auth_user') || 'null');

  const headers = {
    'Accept': 'application/json',
    ...(options.headers || {})
  };

  // Only set Content-Type to JSON if not uploading FormData (multipart)
  if (!(options.body instanceof FormData) && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  // Attach JWT Bearer Token if present
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  // Attach user context headers if present (useful for direct microservice testing)
  if (user) {
    if (user.id) headers['X-User-Id'] = String(user.id);
    if (user.email) headers['X-User-Email'] = user.email;
    if (user.role) headers['X-User-Role'] = user.role;
  }

  // Fallback internal API Key header for service-level operations
  headers['X-API-KEY'] = 'ai-service-secret-key-12345';

  try {
    const response = await fetch(url, {
      ...options,
      headers
    });

    const contentType = response.headers.get('content-type');
    let data = null;

    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (!response.ok) {
      const errorMessage = (data && data.message) ? data.message : `HTTP error ${response.status}: ${response.statusText}`;
      return {
        success: false,
        status: response.status,
        message: errorMessage,
        data: null
      };
    }

    // Standardize response structure
    if (data && typeof data === 'object' && 'success' in data) {
      return data;
    }

    return {
      success: true,
      status: response.status,
      message: 'Operation completed successfully',
      data: data
    };
  } catch (error) {
    console.warn(`API request to ${url} failed, falling back to local handler:`, error.message);
    return {
      success: false,
      isNetworkError: true,
      message: error.message,
      data: null
    };
  }
}

export const apiClient = {
  get: (endpoint, headers) => request(endpoint, { method: 'GET', headers }),
  post: (endpoint, body, headers) => request(endpoint, {
    method: 'POST',
    body: body instanceof FormData ? body : JSON.stringify(body),
    headers
  }),
  put: (endpoint, body, headers) => request(endpoint, {
    method: 'PUT',
    body: body instanceof FormData ? body : JSON.stringify(body),
    headers
  }),
  delete: (endpoint, headers) => request(endpoint, { method: 'DELETE', headers }),
  upload: (endpoint, formData, headers) => request(endpoint, {
    method: 'POST',
    body: formData,
    headers
  })
};
