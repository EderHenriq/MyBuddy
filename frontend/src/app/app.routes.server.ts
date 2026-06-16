import { RenderMode, ServerRoute } from "@angular/ssr";

export const serverRoutes: ServerRoute[] = [
  {
    path: "",
    renderMode: RenderMode.Prerender,
  },
  {
    path: "institucional/**",
    renderMode: RenderMode.Prerender,
  },
  {
    path: "auth/**",
    renderMode: RenderMode.Prerender,
  },
  {
    path: "public/**",
    renderMode: RenderMode.Prerender,
  },
  {
    path: "style-guide",
    renderMode: RenderMode.Prerender,
  },

  {
    path: "**",
    renderMode: RenderMode.Client,
  },
];
