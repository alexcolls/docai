export default defineNuxtRouteMiddleware(() => {
  const { auth } = useStore();
  if (!auth.isAuth || !auth.token) {
    return navigateTo('/login');
  }
});
