package pwr.edu.ilab.utils

sealed class AwaitedResource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): AwaitedResource<T>(data)
    class Error<T>(message: String, data: T? = null): AwaitedResource<T>(data, message)
}
