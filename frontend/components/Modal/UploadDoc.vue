<template>
  <UModal
    v-model="app.modal.isUploadDoc"
    :ui="{ base: '!mt-6', width: 'sm:max-w-fit' }"
  >
    <template #default>
      <UCard>
        <template #header>
          <div class="flex justify-between">
            <UIcon
              name="i-heroicons-bolt"
              class="w-5 h-5 mt-2"
            />
            <div class="pt-1">
              {{ $t('uploadAndProcess') }}
            </div>
            <CommonBtnClose @click="onClose" />
          </div>
        </template>
        <UploadUppy
          id="uploadDoc"
          :height="620"
          :hide-upload-button="true"
          :local-storage="false"
          :max-size="maxSize"
          :max-files="maxFiles"
          @file-added="onFileAdded"
          @file-removed="onFileRemoved"
        />
        <CommonProgressBar
          :progress="uploadProgress"
          :loading="isUploading"
          :wait-to-reset-ms="waitToResetMs"
          :transparent="true"
          class="mt-2 -mb-2"
        />
        <template #footer>
          <div class="flex justify-end">
            <CommonBtnUpload
              :loading="isUploading"
              :disabled="isDisabled"
              class="mr-2"
              @click="onSubmit"
            />
            <CommonBtnCancel
              class="ml-2 h-9"
              @click="onClose"
            />
          </div>
        </template>
      </UCard>
    </template>
  </UModal>
</template>

<script setup lang="ts">

const config = useRuntimeConfig();
const { auth, app, data } = useStore();
const api = useApi();

const waitToResetMs = 2000;
const maxSize = Number(config.public.MAX_FILE_SIZE);
const maxFiles = 22;

const files = ref<UppyFile[]>([]);
const asyncFiles: string[] = [];
const isDisabled = computed(() => files.value.length === 0);

const onFileAdded = (file: UppyFile) => {
  files.value.push(file);
  if (file.size > maxSize) {
    asyncFiles.push(file.name);
  }
};

const onFileRemoved = (file: UppyFile) => {
  const index = files.value.findIndex(f => f.id === file.id);
  if (index !== -1) {
    files.value.splice(index, 1);
    asyncFiles.splice(asyncFiles.indexOf(file.name), 1);
  }
};

const {
  onUpload,
  isUploading,
  uploadProgress,
  resetProgressBar,
  resetUploadResponse
} = useUpload(api.docsURL(), auth.token, true, waitToResetMs, auth.type);

const onSubmit = async () => {
  const fileArray = await onUpload(files.value);
  if (fileArray?.length) {
    app.isAiProcessing = true;
    onClose();
    for (const file of fileArray) {
      if (file.id) {
        try {
          const res = await $fetch<AiResponse>(api.runJobAsyncURL(), {
            method: 'POST',
            headers: {
              Authorization: `${auth.type} ${auth.token}`
            },
            body: {
              documentTypeId: app.selectedDocTypeId,
              documentId: file.id
            }
          });
          if (res) {
            data.chat.push(res);
            app.isAiProcessing = true;
          }
        } catch {}
      }
    }
  }
};

const onClose = async () => {
  app.modal.isUploadDoc = false;
  files.value = [];
  await nextTick();
  resetProgressBar();
  resetUploadResponse();
};

</script>
