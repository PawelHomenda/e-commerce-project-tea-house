export const environment = {
  production: false,
  apiUrl: '/api', // Proxied through nginx to business-server:8080
  authServerUrl: '/auth-server/api',  // Proxied through nginx to auth-server:9000
  debugger : true
};