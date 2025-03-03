<template>
  <div class="cursor-pointer">
    <USelectMenu
      v-model="selectedDocType"
      icon="i-heroicons-document-check-20-solid"
      :popper="{ placement: 'top' }"
      :selected="selectedDocType"
      :options="docTypes"
      :placeholder="$t('selectDoc')"
      class="px-1"
    />
  </div>
</template>

<script setup lang="ts">

const route = useRoute();
const router = useRouter();
const { app, data } = useStore();

if (route.query.id) {
  app.selectedDocTypeId = route.query.id as string;
} else if (app.selectedDocTypeId) {
  router.replace({
    query: {
      ...route.query,
      id: app.selectedDocTypeId
    }
  });
}

const docTypes = computed(() => {
  const sorted = genericSort(data.docTypes, 'name') ?? data.docTypes;
  return sorted.map(d => d.name);
});

const selectedDocType = computed({
  get () {
    if (!app.selectedDocTypeId) { return ''; }
    const found = data.docTypes.find(d => d.id === app.selectedDocTypeId);
    return found?.name || '';
  },
  set (value: string) {
    const found = data.docTypes.find(d => d.name === value);
    app.selectedDocTypeId = found?.id || '';
    router.replace({
      query: {
        ...route.query,
        id: found?.id || ''
      }
    });
    app.refreshChat();
  }
});

</script>
