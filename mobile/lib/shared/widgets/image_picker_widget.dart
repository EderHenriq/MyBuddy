import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/features/pets/presentation/bloc/image_picker_cubit.dart';
import 'package:mybuddy_app/shared/theme/app_colors.dart';

class ImagePickerWidget extends StatelessWidget {
  final void Function(File) onImageSelected;
  final String? label;

  const ImagePickerWidget({
    super.key,
    required this.onImageSelected,
    this.label,
  });

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<ImagePickerCubit, ImagePickerState>(
      listener: (context, state) {
        if (state is ImagePickerSuccess) {
          onImageSelected(state.imageFile);
        }
      },
      builder: (context, state) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (label != null) ...[
              Text(
                label!,
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(height: 8),
            ],
            _buildPreview(state),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _buildButton(
                    icon: Icons.camera_alt,
                    label: 'Câmera',
                    onPressed: () => context.read<ImagePickerCubit>().pickFromCamera(),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _buildButton(
                    icon: Icons.photo_library,
                    label: 'Galeria',
                    onPressed: () => context.read<ImagePickerCubit>().pickFromGallery(),
                  ),
                ),
              ],
            ),
            if (state is ImagePickerSuccess) ...[
              const SizedBox(height: 8),
              TextButton.icon(
                onPressed: () => context.read<ImagePickerCubit>().clear(),
                icon: const Icon(Icons.delete_outline, color: Colors.red),
                label: const Text(
                  'Remover imagem',
                  style: TextStyle(color: Colors.red),
                ),
              ),
            ],
          ],
        );
      },
    );
  }

  Widget _buildPreview(ImagePickerState state) {
    if (state is ImagePickerLoading) {
      return Container(
        height: 200,
        decoration: BoxDecoration(
          color: Colors.grey[200],
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Center(child: CircularProgressIndicator()),
      );
    }

    if (state is ImagePickerSuccess) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: Image.file(
          state.imageFile,
          height: 200,
          width: double.infinity,
          fit: BoxFit.cover,
        ),
      );
    }

    return Container(
      height: 200,
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[400]!),
      ),
      child: const Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.image_outlined, size: 48, color: Colors.grey),
          SizedBox(height: 8),
          Text(
            'Nenhuma imagem selecionada',
            style: TextStyle(color: Colors.grey),
          ),
        ],
      ),
    );
  }

  Widget _buildButton({
    required IconData icon,
    required String label,
    required VoidCallback onPressed,
  }) {
    return ElevatedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon, size: 18),
      label: Text(label),
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(vertical: 12),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
      ),
    );
  }
}