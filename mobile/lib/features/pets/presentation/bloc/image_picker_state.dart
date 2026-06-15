part of 'image_picker_cubit.dart';

abstract class ImagePickerState extends Equatable {
  const ImagePickerState();

  @override
  List<Object?> get props => [];
}

class ImagePickerInitial extends ImagePickerState {}

class ImagePickerLoading extends ImagePickerState {}

class ImagePickerSuccess extends ImagePickerState {
    
  final File imageFile;

  const ImagePickerSuccess(this.imageFile);

  @override
  List<Object?> get props => [imageFile];
}

class ImagePickerCancelled extends ImagePickerState {}