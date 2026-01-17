import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client'
import App from './App.jsx';
import { AuthProvider } from 'oidc-react';
import 'bootstrap/dist/css/bootstrap.min.css';

const oidcConfig = {
    authority: '/realms/scaler',
    redirectUri: window.location.origin,
    postLogoutRedirectUri: window.location.origin,
    clientId: 'react-client',
    scope: 'openid profile offline_access',
    responseType: 'code',
    autoSignIn: true, // Automatically redirect to Keycloak if not logged in
    silentRenew: true,
    useRefreshToken: true,
    renewTimeBeforeTokenExpiresInSeconds: 30,
};

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <AuthProvider {...oidcConfig}>
            <App />
        </AuthProvider>
    </StrictMode>
);