package de.jawb.keysafe.backend.core.base;

public abstract class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public final synchronized Throwable fillInStackTrace() {

        final int stackTraceDepth = stackTraceDepth();

        if (stackTraceDepth >= 0) {

            if (stackTraceDepth == 0) {
                return this;
            }

            super.fillInStackTrace();
            StackTraceElement[] origStackTrace = getStackTrace();
            if (origStackTrace.length >= stackTraceDepth) {
                StackTraceElement[] newStackTrace = new StackTraceElement[stackTraceDepth];
                System.arraycopy(origStackTrace, 0, newStackTrace, 0, stackTraceDepth);
                setStackTrace(newStackTrace);
            }
            return this;
        }

        return super.fillInStackTrace();
    }

    protected int stackTraceDepth() {
        return 4;
    }
}
