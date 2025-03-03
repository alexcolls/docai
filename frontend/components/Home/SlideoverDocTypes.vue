<template>
  <USlideover
    v-model="app.isMenuOpen"
    :ui="{
      overlay: {
        background: 'bg-primary-600/20 dark:bg-primary-800/20'
      }
    }"
    side="left"
    class="!p-0"
  >
    <ModalAddDocType />
    <ModalEditDocType />
    <ModalDeleteDocType />
    <UCard
      :ui="{
        divide: 'divide-y divide-gray-500/20 dark:divide-gray-500/50',
        rounded: 'rounded-none',
        ring: 'ring-gray-300 dark:ring-gray-700',
        header: { base: '!border-none'}
      }"
      class="min-h-screen bg-gray-100 dark:bg-gray-900"
    >
      <template #header>
        <div
          class="flex items-center justify-between -m-0.5 active:!border-none"
          @click="app.selectedDocTypeId = ''"
        >
          <CommonBtnGradient
            icon="i-heroicons-plus"
            @click="app.modal.isAddDocType = true"
          >
            {{ $t('newType') }}
          </CommonBtnGradient>
          <CommonBtnClose @click="onClose" />
        </div>
      </template>
      <div
        class="h-[80vh] w-full p-1 px-0.5 border border-gray-200
          rounded-xl dark:border-gray-700 !overflow-y-scroll shadow-inner
          dark:shadow-gray-700"
      >
        <CommonLoader v-if="status === 'pending'" />
        <div v-else>
          <div v-for="item in data.docTypes" :key="item.id">
            <div
              class="border p-2 border-gray-200 dark:border-gray-700
              m-1 rounded-xl hover:bg-gray-200 hover:cursor-pointer
              flex justify-between hover:dark:bg-gray-800 !shadow-gray-400
              dark:!shadow-gray-600 align-middle items-center"
              :class="app.selectedDocTypeId === item.id
                ? selectedStyle
                : 'border-gray-200'"
              @click="() => {
                app.selectedDocTypeId = item.id;
                app.selectedDocType = item.name;
              }"
            >
              <div class="mt-[2px] ml-2">
                {{ item.name }}
              </div>
              <UDropdown
                :items="rowMenu()"
                :ui="{
                  base: 'w-[120px]'
                }"
                :popper="{
                  placement: 'bottom-start',
                  offsetDistance: -1,
                }"
              >
                <UButton
                  color="gray"
                  variant="ghost"
                  icon="i-heroicons-ellipsis-horizontal-20-solid"
                  class="active:bg-gray-100 dark:active:bg-gray-900
                  hover:bg-transparent dark:hover:bg-transparent border-none
                  active:shadow-inner !shadow-gray-300 dark:!shadow-gray-700"
                />
              </UDropdown>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div
          class="border border-gray-300 dark:border-gray-700 text-sm
            rounded-xl p-2 pl-4 text-gray-600 dark:text-gray-400
            text-center min-h-10 items-center"
        >
          {{ app.selectedDocTypeId ? app.selectedDocTypeId : $t('noSelected') }}
        </div>
      </template>
    </UCard>
  </USlideover>
</template>

<script setup lang="ts">

const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const { auth, data, app } = useStore();
const { duplicateType } = useAlerts();
const api = useApi();

const selectedStyle =
  '!border-primary/50 shadow-inner !bg-primary-200/20 dark:!bg-primary-800/20';

const {
  data: docTypes,
  status,
  refresh
} = await useLazyFetch<DocumentType[]>(api.docTypesURL(), {
  headers: {
    Authorization: `${auth.type} ${auth.token}`
  }
});

onMounted(() => {
  if (route.query.selectedDocTypeId) {
    app.selectedDocTypeId = route.query.id as string;
  } else if (app.selectedDocTypeId) {
    router.replace({
      query: { ...route.query, id: app.selectedDocTypeId }
    });
  }
});

watch(() => app.selectedDocTypeId, (v) => {
  if (!v && route.query.id) {
    const { id, ...rest } = route.query;
    router.replace({ query: { ...rest, id } });
  } else if (v && v !== route.query.id) {
    router.replace({ query: { ...route.query, id: v } });
  }
  app.refreshChat();
}, { immediate: true });

app.refreshDocTypes = refresh;

watch(docTypes, () => {
  if (docTypes.value) {
    data.docTypes.length = 0;
    data.docTypes = genericSort(docTypes.value, 'name') ?? data.docTypes;
  }
});

const rowMenu = () => [
  [{
    label: t('btn.edit'),
    icon: 'i-heroicons-pencil-square-solid',
    click: () => { app.modal.isEditDocType = true; }
  },
  {
    label: t('btn.duplicate'),
    icon: 'i-heroicons-document-duplicate-20-solid',
    click: async () => {
      try {
        const url = api.docTypeIdURL(app.selectedDocTypeId);
        const docType = await $fetch<DocumentType>(url, {
          headers: {
            Authorization: `${auth.type} ${auth.token}`
          }
        });
        if (docType && docType.id) {
          const res = await $fetch<{ id?: string }>(url, {
            method: 'PUT',
            headers: {
              Authorization: `${auth.type} ${auth.token}`
            },
            body: { ...docType, id: undefined }
          });
          if (res && res.id) {
            app.refreshDocTypes();
            duplicateType.success();
          } else {
            throw new Error('No response duplicating document type');
          }
        } else {
          throw new Error('No response getting document type');
        }
      } catch (err) {
        duplicateType.error();
      }
    }
  }],
  [{
    label: t('btn.delete'),
    icon: 'i-heroicons-trash-20-solid',
    click: () => { app.modal.isDeleteDocType = true; }
  }]
];

const lastId = ref('');

watch(() => app.selectedDocTypeId, (v) => {
  if (v) {
    lastId.value = v;
  }
});

const onClose = () => {
  app.isMenuOpen = false;
  setTimeout(() => {
    app.selectedDocTypeId = lastId.value;
  }, 300);
};

</script>
