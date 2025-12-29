import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import fs from "fs";

export default defineConfig({
  plugins: [react()],

  define: {
    global: "window",
  },

  server: {
    https: {
      pfx: fs.readFileSync("./certs/frontend.p12"),
      passphrase: "password",
    },
    port: 5173,
  },
});
