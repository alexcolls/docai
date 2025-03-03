// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  ssr: false,
  app: {
    baseURL: '/',
    head: {
      title: 'INNOCV Smart Doc AI',
      meta: [
        { charset: 'utf-8' },
        {
          name: 'viewport',
          content: 'width=device-width, initial-scale=1'
        },
        {
          hid: 'description',
          name: 'description',
          content: 'INNOCV Smart Doc AI'
        }
      ],
      link: [
        {
          rel: 'icon',
          href: 'favicon.png',
          color: '#000000'
        }
      ]
    },
    pageTransition: { name: 'page', mode: 'out-in' }
  },
  nitro: {
    preset: 'bun'
  },
  runtimeConfig: {
    public: {
      VERSION: process.env.APP_VERSION ?? '1.0.0',
      API_URL: process.env.API_URL ?? 'https://apps.innocv.com/smartdoc-api/',
      MAX_FILE_SIZE: Number(process.env.MAX_FILE_SIZE) ?? 1073741824, /* 1GB */
      REFRESH_INTERVAL: Number(process.env.REFRESH_INTERVAL) ?? 10000 /* 10s */
    }
  },
  googleFonts: {
    display: 'swap',
    preconnect: true,
    families: {
      Orbitron: [400, 500, 700],
      Inter: '200..700'
    }
  },
  imports: {
    dirs: [
      'utils',
      'stores',
      'services'
    ]
  },
  modules: [
    '@nuxtjs/eslint-module',
    '@nuxtjs/google-fonts',
    '@nuxtjs/i18n',
    '@pinia/nuxt',
    '@pinia-plugin-persistedstate/nuxt',
    '@nuxt/ui',
    '@nuxt/image'
  ],
  piniaPersistedstate: {
    storage: 'localStorage'
  },
  ui: {
    icons: [
      'heroicons',
      'svg-spinners',
      'pepicons-pop',
      'material-symbols',
      'circle-flags',
      'line-md'
    ]
  },
  colorMode: {
    preference: 'system'
  },
  i18n: {
    langDir: 'locales/',
    defaultLocale: 'en',
    locales: [
      { code: 'en', iso: 'en-US', file: 'en.ts' },
      { code: 'es', iso: 'es-ES', file: 'es.ts' },
      { code: 'fr', iso: 'fr-FR', file: 'fr.ts' }
    ],
    strategy: 'no_prefix'
  }
});
