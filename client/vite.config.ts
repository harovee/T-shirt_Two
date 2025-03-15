import {ConfigEnv, defineConfig, loadEnv} from 'vite';
import vue from '@vitejs/plugin-vue';
import path from "path";

export default defineConfig(({command, mode}: ConfigEnv) => {
  const env = loadEnv(mode, process.cwd());
  console.log(command);
  console.log(env);
  console.log(mode);
  return {
    css: {
      postcss: './postcss.config.js',
    },
    plugins: [vue()],
    define: {
      __APP_ENV__: JSON.stringify(env.VITE_APP_ENV),
      __API_URL__: JSON.stringify(env.VITE_BASE_URL_CLIENT)
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, "./src"),
        '@views': path.resolve(__dirname, "./src/views"),
      }
    },
    build: {
      outDir: 'dist',
      sourcemap: true,
    },
    server: {
      port: Number(env.VITE_BASE_PORT_CLIENT),
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET,
          changeOrigin: true,
          rewrite: (path: string) => path.replace(/^\/api/, ''),
        },
        '/other-api': {
        target: 'http://localhost:3000',  // Backend 2
        changeOrigin: true,
        secure: false,
        rewrite: (path: string) => path.replace(/^\/other-api/, '')
      },
      }
    },
  };

});
