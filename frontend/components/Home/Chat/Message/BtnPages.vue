<template>
  <div class="flex min-w-32 w-full p-2 justify-between">
    <UButton class="rounded-full pr-4 hidden lg:flex" @click="prevPage">
      <UIcon name="i-heroicons-chevron-left" class="text-xl -ml-1" />
      <div class="ml-1">
        {{ $t('btn.prev') }}
      </div>
    </UButton>
    <UButton
      class="rounded-full flex lg:hidden justify-center w-9 h-9"
      @click="prevPage"
    >
      <UIcon name="i-heroicons-chevron-left" class="text-xl -ml-0.5" />
    </UButton>
    <div class="my-1 flex items-center gap-2">
      <template v-if="!isEditing">
        <span class="cursor-pointer hover:underline" @click="startEditing">
          {{ currentPage }}
        </span>
      </template>
      <template v-else>
        <UInput
          ref="inputEl"
          v-model="editedPage"
          type="number"
          :max="totalPages"
          :min="1"
          class="rounded px-2 text-center"
          :class="totalPages > 9
            ? 'w-20'
            : totalPages > 99
              ? 'w-24'
              : totalPages > 999
                ? 'w-28'
                : 'w-16'"
          @keyup.enter="submitEdit"
          @blur="submitEdit"
        />
      </template>
      <span>/</span>
      <span>{{ totalPages }}</span>
    </div>
    <UButton class="rounded-full pl-4 hidden lg:flex" @click="nextPage">
      <div class="mr-1">
        {{ $t('btn.next') }}
      </div>
      <UIcon name="i-heroicons-chevron-right" class="text-xl -mr-1" />
    </UButton>
    <UButton
      class="rounded-full flex lg:hidden justify-center w-9 h-9"
      @click="nextPage"
    >
      <UIcon name="i-heroicons-chevron-right" class="text-xl -mr-0.5" />
    </UButton>
  </div>
</template>

<script setup lang="ts">

const props = defineProps<{
  currentPage: number;
  totalPages: number;
}>();

const emit = defineEmits(['update:currentPage']);

const isEditing = ref(false);
const editedPage = ref(props.currentPage);
const inputEl = ref<any>(null);

watch(() => props.currentPage, (v) => {
  if (!isEditing.value) {
    editedPage.value = v;
  }
});

const startEditing = async () => {
  isEditing.value = true;
  editedPage.value = props.currentPage;
  await nextTick();
  const nativeInput = inputEl.value?.$el?.querySelector('input') ||
    inputEl.value?.input;
  if (nativeInput) {
    nativeInput.focus();
  } else {
    setTimeout(() => {
      inputEl.value?.$el?.querySelector('input')?.focus();
    }, 0);
  }
};

const submitEdit = () => {
  let pageNum = Number(editedPage.value);
  if (isNaN(pageNum)) {
    pageNum = props.currentPage;
  } else if (pageNum < 1) {
    pageNum = 1;
  } else if (pageNum > props.totalPages) {
    pageNum = props.totalPages;
  }
  emit('update:currentPage', pageNum);
  isEditing.value = false;
};

const nextPage = () => {
  const newPage =
    props.currentPage === props.totalPages ? 1 : props.currentPage + 1;
  emit('update:currentPage', newPage);
};

const prevPage = () => {
  const newPage =
    props.currentPage === 1 ? props.totalPages : props.currentPage - 1;
  emit('update:currentPage', newPage);
};

</script>
