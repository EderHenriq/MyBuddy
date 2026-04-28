import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';

const MyBuddyPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#fff3e0',
      100: '#ffe0b2',
      200: '#ffcc80',
      300: '#ffb74d',
      400: '#f0a346',
      500: '#ff7b00',
      600: '#cc6200',
      700: '#994a00',
      800: '#663100',
      900: '#331900',
      950: '#1a0d00',
    },
    colorScheme: {
      light: {
        primary: {
          color: '{primary.500}',
          contrastColor: '#ffffff',
          hoverColor: '{primary.600}',
          activeColor: '{primary.700}',
        },
      },
      dark: {
        primary: {
          color: '{primary.500}',
          contrastColor: '#3a3737',
          hoverColor: '{primary.600}',
          activeColor: '{primary.700}',
        },
      },
    },
  },
  components: {
    inputtext: {
      root: {
        borderRadius: 'var(--radius-sm)',
        borderColor: 'var(--border-color)',
        hoverBorderColor: '#b3b3b3',
        focusBorderColor: '#2d2d2d',
      },
    },
    checkbox: {
      root: {
        borderRadius: '4px',
        checkedBackground: 'var(--secondary-color)',
        checkedBorderColor: 'var(--secondary-color',
        focusBorderColor: 'var(--secondary-color)',
        checkedHoverBackground: '#4a6b16',
        checkedHoverBorderColor: '#4a6b16',
        focusRing: {
          color: 'var(--secondary-color)',
        },
      },
      icon: {
        checkedColor: '#ffffff',
      },
    },
    button: {
      root: {
        borderRadius: 'var(--radius-sm)',
        secondary: {
          background: '#efeeee',
          borderColor: 'transparent',
          hoverBackground: '#e0dfdf',
          activeBackground: '#d4d3d3',
        },
      },
    },
    inputgroup: {
      addon: {
        background: '#ffffff',
        borderColor: 'var(--border-color)',
        borderRadius: 'var(--radius-sm)',
        color: 'var(--text-color)',
      },
    },
    autocomplete: {
      root: {
        borderRadius: 'var(--radius-sm)',
      },
      dropdown: {
        background: '#ffffff',
        borderColor: 'var(--border-color)',
        borderRadius: 'var(-radius-sm)',
        hoverBackground: '#f5f5f5',
      },
    },
  },
});

export { MyBuddyPreset };
