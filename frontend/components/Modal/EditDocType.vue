<template>
  <UModal v-model="app.modal.isEditDocType">
    <UCard>
      <template #header>
        <div class="flex justify-between">
          <div class="w-6" />
          <div class="pt-1">
            {{ $t('btn.edit') }} {{ app.selectedDocType }}
          </div>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <CommonLoaderSlot
        :is-loading="isDataLoading || app.isUppyLoading"
        :height="700"
      >
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
            :ui="{
              base: 'h-42'
            }"
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
        <UFormGroup :label="$t('samples')" class="m-1 mt-4">
          <UploadUppy
            :id="app.selectedDocTypeId ?? getRandomInt(9999)"
            :key="key"
            :url="api.docsURL()"
            class="mt-2"
            :selected-id="app.selectedDocTypeId"
            :samples="originalSamples"
            :height="300"
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
      </CommonLoaderSlot>
      <template #footer>
        <div class="flex justify-end">
          <CommonBtnEdit
            :loading="isLoading"
            :disabled="isDisabled || isLoading"
            class="mr-2"
            @click="onEdit"
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

const { app, auth } = useStore();
const { editType } = useAlerts();
const api = useApi();

const key = ref(0);
const tabsKey = ref(0);
const waitToResetMs = 2000;

const name = ref('');
const prompt = ref('');
const processPageByPage = ref(true);
const files = ref<UppyFile[]>([]);
const samples = ref<DocumentSample[]>([]);
const sampleResponses = ref<string[]>([]);

const originalName = ref('');
const originalPrompt = ref('');
const originalProcessPageByPageValue = ref(true);
const originalFiles = ref<UppyFile[]>([]);
const originalSamples = ref<DocumentSample[]>([]);
const originalSampleResponses = ref<string[]>([]);

const isMounting = ref(true);
const isDataLoading = ref(false);
const isLoading = ref(false);

const isFiles = computed(() => files.value.length > 0);
const filesItems = computed(() => files.value.map(file => ({
  label: file.name
})));

watch(() => app.modal.isEditDocType, async (v) => {
  if (v) {
    isDataLoading.value = true;
    try {
      const url = api.docTypeIdURL(app.selectedDocTypeId);
      const res = await $fetch<DocumentType>(url, {
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        }
      });
      if (res && res.id) {
        name.value = res.name;
        prompt.value = res.prompt;
        originalProcessPageByPageValue.value = res.processPageByPage;
        processPageByPage.value = res.processPageByPage;
        originalName.value = res.name;
        originalPrompt.value = res.prompt;
        if (res.samples && res.samples.length) {
          originalSamples.value = res.samples;
          samples.value = res.samples;
          originalSampleResponses.value = res.samples.map(
            sample => sample.response);
          sampleResponses.value = res.samples.map(
            sample => sample.response);
          key.value++;
        }
      }
      isDataLoading.value = false;
      setTimeout(() => {
        isMounting.value = false;
      }, 2000);
    } catch {}
  }
}, { immediate: true });

const onFileAdded = (file: UppyFile) => {
  if (samples.value && samples.value.length) {
    const isNewFile = !samples.value.some(sample =>
      sample.document.name === file.name);
    files.value.push(file);
    if (isNewFile) {
      sampleResponses.value.push('');
    } else {
      originalFiles.value.push(file);
    }
  }
  tabsKey.value++;
};

const onFileRemoved = (file: UppyFile) => {
  const index = files.value.findIndex(f => f.id === file.id);
  if (index !== -1) {
    files.value.splice(index, 1);
    samples.value.splice(index, 1);
    sampleResponses.value.splice(index, 1);
    originalSampleResponses.value.splice(index, 1);
  }
  tabsKey.value++;
};

const {
  onUpload,
  uploadProgress
} = useUpload(api.docsURL(), auth.token, false, waitToResetMs, auth.type);

const isDisabled = computed(() => {
  const hasNameChanged = name.value !== originalName.value;
  const hasPromptChanged = prompt.value !== originalPrompt.value;
  const hasProcessPageByPageChanged = processPageByPage.value !==
    originalProcessPageByPageValue.value;
  const hasFilesChanged = files.value.length !== originalFiles.value.length ||
    files.value.some(file => !originalFiles.value.find(
      origFile => origFile.id === file.id));
  const haveResponsesChanged = sampleResponses.value.some(
    (res, index) => res !== originalSampleResponses.value[index]
  );
  const isSampleResEmpty = (!files.value.length && !samples.value.length)
    ? false
    : sampleResponses.value.some((res, index) => !res && (files.value[index] ||
      samples.value[index]));
  return isMounting.value || isSampleResEmpty || isDataLoading.value ||
         (!hasNameChanged && !hasPromptChanged && !hasFilesChanged &&
          !haveResponsesChanged && !hasProcessPageByPageChanged);
});

const onEdit = async () => {
  if (isLoading.value) { return; }
  isLoading.value = true;
  try {
    const newFiles = files.value.filter(file =>
      !originalFiles.value.some(origFile => origFile.id === file.id)
    );
    if (newFiles.length) {
      const multiplier = 3;
      const resFiles = await onUpload(newFiles);
      uploadProgress.value -= (resFiles.length + 1) * multiplier;
      if (resFiles && resFiles.length) {
        for (let i = 0; i < resFiles.length; i++) {
          const sample = await $fetch<DocumentSample>(api.samplesURL(), {
            method: 'POST',
            headers: { Authorization: `${auth.type} ${auth.token}` },
            body: {
              document: { ...resFiles[i] },
              response: sampleResponses.value[files.value.indexOf(newFiles[i])]
            }
          });
          samples.value.push(sample);
          uploadProgress.value += multiplier;
        }
      }
    }
    for (let i = 0; i < samples.value.length; i++) {
      if (sampleResponses.value[i] !== originalSampleResponses.value[i]) {
        const sample = samples.value[i];
        sample.response = sampleResponses.value[i];
        await $fetch(api.samplesURL(), {
          method: 'PUT',
          headers: { Authorization: `${auth.type} ${auth.token}` },
          body: sample
        });
      }
    }
    const res = await $fetch<{ id?: string }>(
      api.docTypeIdURL(app.selectedDocTypeId), {
        method: 'PUT',
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        },
        body: {
          id: app.selectedDocTypeId,
          name: name.value,
          prompt: prompt.value,
          samples: samples.value
        }
      }
    );
    uploadProgress.value = 100;
    if (res && res.id) {
      await app.refreshDocTypes();
      editType.success();
      onClose();
    } else {
      throw new Error('Failed to edit document type');
    }
  } catch (err) {
    editType.error();
  }
  isLoading.value = false;
};

const onResetForm = () => {
  originalName.value = '';
  originalPrompt.value = '';
  originalFiles.value = [];
  originalSamples.value = [];
  originalSampleResponses.value = [];
  name.value = '';
  prompt.value = '';
  files.value = [];
  samples.value = [];
  sampleResponses.value = [];
};

const onClose = () => {
  app.modal.isEditDocType = false;
};

watch(() => app.modal.isEditDocType, (v) => {
  if (!v) {
    onResetForm();
  }
});

</script>
