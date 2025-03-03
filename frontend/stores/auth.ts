export const useAuthStore = defineStore('smartdoc.auth', {
  persist: true,
  state: () => ({
    isAuth: false,
    type: 'Basic',
    token: '',
    user: {}
  }),
  actions: {
    logout () {
      this.isAuth = false;
      this.token = '';
      this.user = {};
      navigateTo('/login');
    }
  }
});

export default useAuthStore;
