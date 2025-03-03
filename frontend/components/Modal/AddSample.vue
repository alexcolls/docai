<template>
  <UModal v-model="app.modal.isAddSample">
    <UCard
      :ui="{ divide: 'divide-y divide-gray-100 dark:divide-gray-800' }"
      class="!m-0 !p-0"
    >
      <template #header>
        <div class="flex items-center justify-between">
          <div class="w-5" />
          <h3
            class="font-semibold leading-6 text-gray-900 dark:text-white"
          >
            {{ $t('addNewSample') }}
          </h3>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <UTextarea
        v-model="editResponse"
        :rows="22"
      />
      <template #footer>
        <div class="flex justify-end">
          <UButton
            variant="solid"
            :label="$t('addSample')"
            :loading="isLoading"
            icon="i-heroicons-document-plus-20-solid"
            class="pl-4 pr-6 transition-colors duration-300 ease-in-out"
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
const { editResult } = useAlerts();
const api = useApi();

const editResponse = ref(safeParseJSON(app.addSampleJson));

watch(() => app.addSampleJson, () => {
  editResponse.value = safeParseJSON(app.addSampleJson);
});

const isLoading = ref(false);

const onEdit = async () => {
  if (isLoading.value) { return; }
  isLoading.value = true;
  try {
    const document = await $fetch<DocumentUploaded>(
      api.docIdURL(app.selectedDocId), {
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        }
      }
    );
    if (!document || !document.id) {
      throw new Error('No response getting document');
    }
    const newSample = await $fetch<DocumentSample>(api.samplesURL(), {
      method: 'POST',
      headers: {
        Authorization: `${auth.type} ${auth.token}`
      },
      body: {
        document,
        response: editResponse.value
      }
    });
    if (!newSample || !newSample.id) {
      throw new Error('No response creating sample');
    }
    const docType = await $fetch<DocumentType>(
      api.docTypeIdURL(app.selectedDocTypeId), {
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        }
      }
    );
    if (!docType || !docType.id) {
      throw new Error('No response getting document type');
    }
    if (!docType.samples) {
      docType.samples = [];
    }
    docType.samples.push(newSample);
    const res = await $fetch<{ id?: string }>(
      api.docTypeIdURL(app.selectedDocTypeId), {
        method: 'PUT',
        headers: {
          Authorization: `${auth.type} ${auth.token}`
        },
        body: { ...docType }
      }
    );
    if (res && res.id) {
      editResult.success();
      onClose();
    } else {
      throw new Error('Failed to edit document type');
    }
  } catch (err) {
    editResult.error();
  }
  isLoading.value = false;
};

const onClose = () => {
  app.modal.isAddSample = false;
  app.addSampleJson = '';
};

watch(() => app.modal.isAddSample, () => {
  if (!app.modal.isAddSample) {
    app.addSampleJson = '';
  }
});

</script>
