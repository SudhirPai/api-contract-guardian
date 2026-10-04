import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import App from './App';

const theme = createTheme({
    palette: { primary: { main: '#185ADB' }, background: { default: '#F5F7FB' } },
    shape: { borderRadius: 10 },
    typography: { fontFamily: 'Inter, system-ui, sans-serif' }
});
ReactDOM.createRoot(document.getElementById('root')!).render(<React.StrictMode><QueryClientProvider
    client={new QueryClient()}><ThemeProvider
        theme={theme}><CssBaseline /><BrowserRouter><App /></BrowserRouter></ThemeProvider></QueryClientProvider></React.StrictMode>);
