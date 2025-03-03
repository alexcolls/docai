<template>
  <div
    class="mt-2.5 p-1 border border-gray-300 dark:border-gray-700
      rounded-xl text-base text-gray-600 dark:text-gray-400 shadow-inner
      shadow-primary-400 dark:shadow-primary-600 bg-gray-100/80
      dark:bg-gray-900/60 h-[87vh] flex flex-col relative"
  >
    <div
      ref="chatRef"
      class="flex-grow overflow-y-scroll h-full p-2 transition-all duration-300
        justify-start !align-top !items-start"
    >
      <CommonLoaderSlot
        :is-loading="isLoading"
        :height-full="true"
        icon-class="text-6xl"
        :futuristic="true"
        :model="app.selectedAiEngineName"
      >
        <CommonAiLoader
          v-if="app.isAiProcessing && (!data.chat?.length ||
            !data.chat[0]?.resultPage?.length ||
            !data.chat[data.chat?.length - 1]?.resultPage?.length)"
          model="Smart Doc"
          class="min-h-[650px]"
        />
        <div
          v-if="data.chat?.length"
          class="flex flex-col justify-end !z-[-1]"
        >
          <div
            v-for="e in data.chat"
            :key="`${app.selectedDocTypeId}_${e.id}`"
          >
            <HomeChatMessage :ai-response="e" />
          </div>
          <div class="h-20 w-full" />
        </div>
        <div
          v-else-if="app.selectedDocTypeId"
          class="p-3 w-full h-full rounded-lg ml-0 text-sm
            text-center absolute flex items-center justify-center"
        >
          <div>
            <UIcon
              name="i-material-symbols-drive-folder-upload-rounded"
              class="h-20 w-20"
            />
            <div class="mt-2">
              {{ $t('emptyChat').split('.')[0] ?? 'Empty chat' }}.
            </div>
            <div class="mt-1">
              {{ $t('emptyChat').split('.')[1] ?? 'Upload a document' }}.
            </div>
          </div>
        </div>
        <div
          v-else
          class="p-3 w-full h-full rounded-lg ml-0 text-sm
            text-center absolute flex items-center justify-center"
        >
          <div>
            <UIcon name="i-heroicons-no-symbol-16-solid" class="h-8 w-8" />
            <div class="mt-2">
              {{ $t('docNoSelected').split('.')[0] ??
                'No document type selected' }}.
            </div>
            <div class="mt-1">
              {{ $t('docNoSelected').split('.')[1] ??
                'Select a document type' }}.
            </div>
          </div>
        </div>
      </CommonLoaderSlot>
      <div class="w-full absolute bottom-0 z-40 -ml-3">
        <HomeChatMenu />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

const config = useRuntimeConfig();
const { app, data } = useStore();

const chatRef = ref<HTMLElement>();
const isLoading = ref(false);

app.scrollToTop = () => scrollToTopSmoothly(chatRef.value);
app.refreshChat = async () => {
  isLoading.value = true;
  app.isAiProcessing = false;
  await data.fetchChat(app.selectedDocTypeId);
  isLoading.value = false;
};

watch(() => app.selectedDocTypeId, async () => {
  if (chatRef.value) {
    scrollToTop(chatRef.value);
  }
  if (app.selectedDocTypeId) {
    await app.refreshChat();
    app.scrollToTop();
  }
}, { immediate: true });

watch(() => app.isAiProcessing, async () => {
  if (data.chat?.length) {
    app.isAiProcessing = data.findProcessingResults();
  }
  while (app.isAiProcessing) {
    await sleep(config.public.REFRESH_INTERVAL ?? 10000);
    await data.fetchChat(app.selectedDocTypeId);
    app.isAiProcessing = data.findProcessingResults();
  }
}, { immediate: true });

</script>
