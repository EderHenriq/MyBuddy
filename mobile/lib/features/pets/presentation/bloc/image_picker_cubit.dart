import 'dart:io';

import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:mybuddy_app/core/services/image_picker_service.dart';

part 'image_picker_state.dart';

class ImagePickerCubit extends Cubit<ImagePickerState> {
 
  final ImagePickerService _service;

  ImagePickerCubit({required ImagePickerService service})
    :_service = service,
    super(ImagePickerInitial());

  Future<void> pickFromCamera() async {
    emit(ImagePickerLoading());
    final file = await _service.pickFromCamera();
    _handleResult(file);
  }

  Future<void> pickFromGallery() async {
    emit(ImagePickerLoading());
    final file = await _service.pickFromGallery();
    _handleResult(file);
  }

  void _handleResult(File? file) {
    if (file != null) {
      emit(ImagePickerSuccess(file));
    } else {
      emit(ImagePickerCancelled());
    }
  }

  void clear() => emit(ImagePickerCancelled());
    
}