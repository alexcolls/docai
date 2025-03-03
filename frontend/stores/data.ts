export const useDataStore = defineStore('smartdoc.data', {
  persist: false,
  state: () => ({
    docTypes: [] as DocumentTypes,
    chat: [] as DocTypeChat
  }),
  actions: {
    async fetchChat (docTypeId: string) {
      const api = useApi();
      const auth = useAuthStore();
      const res = await $fetch<DocTypeChat>(api.chatURL(docTypeId), {
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        }
      });
      if (res) {
        this.chat = genericSort(res, 'createdAt', 'desc') ?? [...res];
      }
    },
    findProcessingResults () {
      return this.chat.some(res =>
        !res?.status || res.status === 'PROCESSING' ||
        (res.resultPage && res.resultPage.some(
          result => !('status' in result) || result.status === 'PROCESSING'))
      );
    }
  }
});

export default useDataStore;
