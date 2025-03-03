<template>
  <UModal v-model="app.modal.isAddDocType">
    <UCard>
      <template #header>
        <div class="flex justify-between">
          <div class="w-6" />
          <div class="pt-1">
            {{ $t('addDocType') }}
          </div>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <UFormGroup :label="$t('name')" class="m-1" required>
        <UInput
          v-model="name"
          :placeholder="$t('addName')"
          required
        />
      </UFormGroup>
      <UFormGroup :label="$t('prompt')" class="m-1 mt-4" required>
        <UTextarea
          v-model="prompt"
          :placeholder="$t('addPrompt')"
          required
          :rows="12"
        />
      </UFormGroup>
      <div class="flex mt-2 ml-1">
        <UToggle
          v-model="processPageByPage"
          class="mt-2"
        />
        <UFormGroup
          v-if="processPageByPage"
          :label="$t('processPageByPage')"
          class="mt-2 ml-4"
          required
        />
        <UFormGroup
          v-else
          :label="$t('processSinglePage')"
          class="mt-2 ml-4"
          required
        />
      </div>
      <UFormGroup
        :label="$t('samples')"
        class="m-1 mt-4"
      >
        <UploadUppy
          id="addSample"
          :height="300"
          :hide-upload-button="true"
          :url="docsURL"
          @file-added="onFileAdded"
          @file-removed="onFileRemoved"
        />
      </UFormGroup>
      <UFormGroup
        v-if="isFiles"
        :label="$t('expResponse')"
        class="m-1 mt-4"
        required
      >
        <UTabs :key="tabsKey" :items="filesItems">
          <template #item="{ index }">
            <UTextarea
              v-model="sampleResponses[index]"
              :placeholder="$t('addResponse')"
              :required="isFiles"
              :rows="8"
            />
          </template>
        </UTabs>
      </UFormGroup>
      <CommonProgressBar
        v-if="files.length"
        :progress="uploadProgress"
        :loading="isLoading"
        :transparent="true"
        :wait-to-reset-ms="waitToResetMs"
        class="mt-3 -mb-2"
      />
      <template #footer>
        <div class="flex justify-end">
          <CommonBtnSubmit
            :loading="isLoading"
            :disabled="isDisabled || isLoading"
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
  </UModal>
</template>

<script setup lang="ts">

const api = useApi();
const url = api.docTypesURL();
const docsURL = api.docsURL();

const { app, auth } = useStore();
const { addType } = useAlerts();

const name = ref('');
const prompt = ref('');
const processPageByPage = ref(true);
const samples = ref<DocumentSample[]>([]);
const files = ref<UppyFile[]>([]);
const isFiles = computed(() => files.value.length > 0);
const filesItems = computed(() => files.value.map(file => ({
  label: file.name
})));
const tabsKey = ref(0);
const sampleResponses = ref<string[]>([]);
const waitToResetMs = 2000;
const isLoading = ref(false);
const isDisabled = computed(() => {
  const isSampleResEmpty = sampleResponses.value.some(res => !res);
  return !name.value || !prompt.value || isSampleResEmpty;
});

const onFileAdded = (file: UppyFile) => {
  files.value.push(file);
  sampleResponses.value.push('');
  tabsKey.value++;
};

const onFileRemoved = (file: UppyFile) => {
  const index = files.value.findIndex(f => f.id === file.id);
  if (index !== -1) {
    files.value.splice(index, 1);
    sampleResponses.value.splice(index, 1);
  }
  tabsKey.value++;
};

const {
  onUpload,
  uploadProgress
} = useUpload(docsURL, auth.token, false, waitToResetMs, auth.type);

const onSubmit = async () => {
  if (isLoading.value) { return; }
  isLoading.value = true;
  try {
    if (files.value && files.value.length) {
      const multiplier = 3;
      const resFiles = await onUpload(files.value);
      uploadProgress.value -= (resFiles.length + 1) * multiplier;
      if (resFiles && resFiles.length) {
        for (let i = 0; i < resFiles.length; i++) {
          samples.value.push(await $fetch<DocumentSample>(api.samplesURL(), {
            method: 'POST',
            headers: { Authorization: `${auth.type} ${auth.token}` },
            body: {
              document: { ...resFiles[i] },
              response: sampleResponses.value[i]
            }
          }));
          uploadProgress.value += multiplier;
        }
      }
    }
    const res = await $fetch<{ id?: string }>(url, {
      method: 'POST',
      headers: { Authorization: `${auth.type} ${auth.token}` },
      body: {
        name: name.value,
        prompt: prompt.value,
        processPageByPage: processPageByPage.value,
        samples: samples.value
      }
    });
    uploadProgress.value = 100;
    if (res?.id) {
      app.selectedDocTypeId = res.id;
      app.isAiProcessing = false;
      await app.refreshDocTypes();
      addType.success();
      onClose();
    } else {
      throw new Error('Failed to add document type');
    }
  } catch (err) {
    addType.error();
  }
  isLoading.value = false;
};

const onResetForm = () => {
  name.value = '';
  prompt.value = '';
  samples.value = [];
  files.value = [];
  sampleResponses.value = [];
};

const onClose = () => {
  app.modal.isAddDocType = false;
};

watch(() => app.modal.isAddDocType, (v) => {
  if (!v) {
    onResetForm();
  }
});

</script>
