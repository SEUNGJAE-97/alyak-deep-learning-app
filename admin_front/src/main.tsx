import {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import loginPillLogo from './assets/images/login-pill-logo.png';

const faviconLink = document.querySelector("link[rel='icon']") ?? document.createElement("link");
faviconLink.setAttribute("rel", "icon");
faviconLink.setAttribute("type", "image/png");
faviconLink.setAttribute("href", loginPillLogo);
if (!faviconLink.parentElement) {
  document.head.appendChild(faviconLink);
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
