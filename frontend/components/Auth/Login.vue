<template>
  <div
    class="w-96 sm:w-[520px] space-y-6 border
      backdrop-filter backdrop-blur-xl rounded-2xl border-primary-500
      transition-all duration-500 !bg-gray-200/20 dark:!bg-gray-800/20
      shadow-lg shadow-primary-700/40 dark:shadow-primary-300/40
      rounded-tl-none rounded-br-none px-8 sm:px-12"
    :style="{ height: panelHeight }"
    @mouseenter="handleHover(true)"
    @mouseleave="handleHover(false)"
  >
    <transition name="fade-scale" mode="out-in">
      <div v-if="isError" key="error" class="w-full text-center pt-6">
        <UIcon
          name="i-heroicons-lock-closed-solid"
          class="h-16 w-16 text-red-500 animate-pulse"
        />
      </div>
      <div v-else-if="isLogin" key="success" class="w-full text-center pt-6">
        <UIcon
          name="i-heroicons-lock-open-solid"
          class="h-16 w-16 text-green-500 animate-pulse"
        />
      </div>
      <div v-else key="login">
        <CommonTxtColor
          class="text-center text-primary-800 dark:text-primary-200
            mt-10 uppercase tracking-widest"
        >
          {{ $t('login') }}
        </CommonTxtColor>
      </div>
    </transition>
    <transition name="fade">
      <UForm
        v-if="isHovered"
        :schema="schema"
        :state="state"
        class="space-y-4 pt-4"
        @submit="onSubmit"
      >
        <UFormGroup
          :label="$t('user')"
          :ui="{ label: { base: '!text-black dark:!text-white' } }"
        >
          <UInput
            v-model="state.user"
            variant="none"
            required
            autocomplete="username"
            class="border border-primary rounded-md text-primary-500
              transition-all duration-500"
            icon="i-heroicons-user"
            :ui="{
              icon: { base: '!text-primary-500' },
              base: '!bg-white/80 dark:!bg-black/80'
            }"
          />
        </UFormGroup>
        <transition name="fade">
          <UFormGroup
            v-if="validUsername"
            :label="$t('pass')"
            :ui="{ label: { base: '!text-black dark:!text-white' } }"
          >
            <UInput
              v-model="state.pass"
              variant="none"
              type="password"
              required
              autocomplete="current-password"
              class="border border-primary rounded-md text-primary-500
                transition-all duration-500"
              icon="i-heroicons-key"
              :ui="{
                icon: { base: '!text-primary-500' },
                base: '!bg-white/80 dark:!bg-black/80'
              }"
            />
          </UFormGroup>
        </transition>
        <transition name="fade">
          <div
            v-if="validUsername && validPassword"
            class="flex justify-center pt-6"
          >
            <CommonBtnGradient
              type="submit"
              class="group px-12 pr-14 py-2 transition-all duration-300
                ease-in-out rounded-full hover:shadow-lg shadow-white"
              icon="i-heroicons-rocket-launch-16-solid"
            >
              {{ $t('btn.go') }}
            </CommonBtnGradient>
          </div>
        </transition>
      </UForm>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { z } from 'zod';
import audFX1 from '~/assets/aud/machine-1.mp3';
import audFX2 from '~/assets/aud/check.flac';
import audFX3 from '~/assets/aud/machine-3.mp3';

const { auth, app, data, ui } = useStore();
const { login } = useAlerts();
const api = useApi();

const state = reactive({
  user: '',
  pass: ''
});

const isHovered = ref(false);
const isHoveredAllowed = ref(true);
const audioHover = new Audio(audFX1);
const audioLogin = new Audio(audFX2);
const audioError = new Audio(audFX3);

const handleHover = (hovering: boolean) => {
  if (hovering && isHoveredAllowed.value) {
    isHovered.value = hovering;
    ui.playAudio(audioHover);
  } else {
    isHovered.value = false;
  }
};

const validUsername = computed(() => state.user.length > 3);
const validPassword = computed(() => state.pass.length > 8);

watch(validUsername, (v) => {
  if (v) {
    ui.playAudio(audioHover);
  }
});

watch(validPassword, (v) => {
  if (v) {
    ui.playAudio(audioHover);
  }
});

const panelHeight = computed(() => {
  if (!isHovered.value) {
    return '120px';
  }
  if (validUsername.value) {
    return validPassword.value ? '360px' : '280px';
  }
  return '220px';
});

const schema = z.object({
  user: z.string(),
  pass: z.string()
});

const loginTime = 2000;
const errorTime = 5000;
const isError = ref(false);
const startError = () => {
  login.error();
  isError.value = true;
  isHovered.value = false;
  isHoveredAllowed.value = false;
  audioError.volume = ui.volumeFX;
  ui.playAudio(audioError);
};

const endError = async () => {
  await sleep(errorTime);
  isError.value = false;
  isHoveredAllowed.value = true;
};

const onSubmit = async () => {
  const token = btoa(`${state.user}:${state.pass}`);
  const authorization = `${auth.type} ${token}`;
  try {
    const res = await $fetch<DocumentTypes>(api.loginURL(), {
      headers: {
        Authorization: authorization
      }
    });
    if (res) {
      data.docTypes.length = 0;
      data.docTypes = genericSort(res, 'name') ?? [...res];
      const docTypeId = app.selectedDocTypeId
        ? app.selectedDocTypeId
        : data.docTypes.length > 0 ? data.docTypes[0].id : '';
      app.selectedDocTypeId = docTypeId;
      const chat = await $fetch<DocTypeChat>(api.chatURL(docTypeId), {
        headers: {
          Authorization: `${auth.type} ${token}`
        }
      });
      if (chat) {
        logIn(token);
        data.chat.length = 0;
        data.chat = genericSort(chat, 'createdAt') ?? [...chat];
        await sleep(loginTime);
        isLogin.value = false;
        navigateTo('/');
        return;
      }
    }
    throw new Error('Failed to login');
  } catch (err) {
    startError();
    auth.isAuth = false;
    auth.token = '';
    data.docTypes = [];
    await endError();
  }
};

const isLogin = ref(false);
const logIn = (token: string) => {
  isLogin.value = true;
  isHovered.value = false;
  isHoveredAllowed.value = false;
  audioLogin.volume = ui.volumeFX;
  ui.playAudio(audioLogin);
  auth.isAuth = true;
  auth.$patch({ token });
  login.success();
};

</script>

<style scoped>
div {
  transition: height 0.5s ease-in-out;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s ease-in-out;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: opacity 0.4s ease-out, transform 0.4s ease-out;
}

.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
