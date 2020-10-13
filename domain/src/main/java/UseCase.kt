import androidx.annotation.CallSuper
import com.pomodorotime.data.ErrorResponse
import com.pomodorotime.data.ResultWrapper
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: P): ResultWrapper<R> {
        return withContext(coroutineDispatcher) {
            try {
                ResultWrapper.Success(execute(parameters))
            } catch (throwable: Throwable) {
                manageException(throwable)
            }
        }
    }

    @CallSuper
    private fun <R> manageException(throwable: Throwable): ResultWrapper<R> {
        return when (throwable) {
            is IOException -> ResultWrapper.NetworkError
            else -> {
                ResultWrapper.GenericError(
                    null,
                    ErrorResponse(message = throwable.message ?: "")
                )
            }
        }
    }

    protected abstract suspend fun execute(parameters: P): R
}