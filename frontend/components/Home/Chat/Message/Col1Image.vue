<template>
  <div class="!sticky top-2 self-start w-1/3 min-w-52 px-1">
    <CommonImgViewer
      :url="imageUrl"
      :alt="api.docIdDownloadURL(document.id)"
      :metadata="document"
      :show-name="false"
      :show-date="false"
      :download-url="api.docIdDownloadURL(document.id)
        ?? api.docIdPageImgURL(document.id, resultPage.page ?? 1)"
      height="h-[220px] sm:h-[420px]"
      class="mt-1 mb-2 rounded-4xl min-w-52 h-full rounded-lg opacity-90"
    />
    <HomeChatMessageBtnPages
      v-if="totalPages > 1"
      :current-page="currentPage"
      :total-pages="totalPages"
      @update:current-page="updatePage"
    />
    <HomeChatMessageGridStats
      :result-page="resultPage"
      :document="document"
    />
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  resultPage: ResultPage;
  document: DocumentUploaded;
  totalPages: number;
  currentPage: number;
}>();

const emit = defineEmits(['update:currentPage']);

const { app, data } = useStore();
const api = useApi();

const processPageByPage = computed(() => {
  return data.docTypes.find(e =>
    e.id === app.selectedDocTypeId)?.processPageByPage ?? false;
});

const currentPage = ref(props.currentPage);

const updatePage = (newPage: number) => {
  currentPage.value = newPage;
  emit('update:currentPage', newPage);
};

watch(() => props.currentPage, (v) => {
  currentPage.value = v;
});

const imageUrl = computed(() => {
  if (processPageByPage.value) {
    return props.totalPages > 1
      ? api.docIdPageImgURL(props.document.id, props.resultPage.page)
      : api.docIdDownloadURL(props.document.id);
  }
  return props.document.extension === 'pdf'
    ? api.docIdPageImgURL(props.document.id, 1)
    : api.docIdDownloadURL(props.document.id);
});

</script>
