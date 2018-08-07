package org.supersrsFP.lulzio;

import java.util.function.Function;
import java.util.function.Supplier;

import scala.Function0;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;
import scala.Function1;

abstract class JIO<A> {

    abstract public int tag();

    static final class Tags {
        static final int Bind = 0;
        static final int Pure = 1;
        static final int Map = 2;
        static final int Effect = 3;
        static final int Fail = 4;
    }

    public static <B> JIO<B> pure(B b){
        return new PureJIO<>(b);
    }

    public static <B> JIO<B> raiseError(Throwable t){
        return new Failed<>(t);
    }

    public static <B> JIO<B> delay(Supplier<B> b){
        return new DelayJIO<>(b);
    }

    public static <B> JIO<B> scalaDelay(Function0<B> b) {
        return new DelayJIO<>(new Supplier<B>() {
            @Override
            public B get() {
                return b.apply();
            }
        });
    }

    private static final Function1<Object, JIO<Object>> ATTEMPT = new Function1<Object, JIO<Object>>() {
        @Override
        public JIO<Object> apply(Object v1) {
            return new PureJIO<>(Right.apply(v1));
        }
    };

    @SuppressWarnings("unchecked")
    public JIO<Either<Throwable, A>> attempt() {
        switch (this.tag()) {
            case Tags.Pure:
                return new PureJIO<>(new Right(((PureJIO<A>) this).getVal()));
            case Tags.Effect:
                return new DelayJIO<>(() -> {
                    try {
                        return new Right<>(((DelayJIO<A>) this).getF().get());
                    } catch (Throwable e) {
                        if (e instanceof VirtualMachineError)
                            throw e;
                        else
                            return new Left<>(e);
                    }
                });
            case Tags.Bind:
                return new BindJIO<>(ATTEMPT, (JIO<Object>) this);
            case Tags.Map:
                return new BindJIO<>(ATTEMPT, (JIO<Object>) this);
            default:
                return new PureJIO<>(new Left<>(((Failed<A>) this).get()));
        }
    }

    final public JIO<A> handleError(Function1<Throwable, A> f) {
        return this.attempt().flatMap(a -> {
            if (a.isRight()) {
                return new PureJIO<>(((Right<Throwable, A>) a).value());
            } else {
                return new PureJIO<>(f.apply(((Left<Throwable, A>) a).value()));
            }
        });
    }

    final public JIO<A> handleErrorWith(Function1<Throwable, JIO<A>> f) {
        return this.attempt().flatMap(a -> {
            if (a.isRight()) {
                return new PureJIO<>(((Right<Throwable, A>) a).value());
            } else {
                return f.apply(((Left<Throwable, A>) a).value());
            }
        });
    }

    @SuppressWarnings("unchecked")
    final public <B> JIO<B> map(Function1<A, B> f) {
        switch (this.tag()) {
            case Tags.Fail:
                return (JIO<B>) this;
            default:
                return new MapJIO<>(f, this);
        }
    }


    @SuppressWarnings("unchecked")
    final public <B> JIO<B> flatMap(Function1<A, JIO<B>> f) {
        switch (this.tag()) {
            case Tags.Fail:
                return (JIO<B>) this;
            default:
                return typedBind(f, this);
        }
    }

    @SuppressWarnings("unchecked")
    private static <B, C> JIO<C> typedBind(Function1<B, JIO<C>> f, JIO<B> old) {
        return new BindJIO<>(f, (JIO<Object>) old);
    }

    @SuppressWarnings("unchecked")
    private static <B, C> JIO<C> attempt(JIO<B> old) {
        return new BindJIO<>(ATTEMPT, (JIO<Object>) old);
    }

    static final class BindJIO<B, C> extends JIO<C> {
        private Object f;
        private JIO<B> old;

        @SuppressWarnings("unchecked")
        BindJIO(Object f, JIO<Object> old) {
            this.f = f;
            this.old = (JIO<B>) old;
        }

        @SuppressWarnings("unchecked")
        public Function1<B, JIO<C>> getF() {
            return (Function1<B, JIO<C>>) f;
        }

        public JIO<B> getOld() {
            return old;
        }

        @Override
        final public int tag() {
            return Tags.Bind;
        }
    }

    static final class PureJIO<A> extends JIO<A> {
        private A val;

        PureJIO(A val) {
            this.val = val;
        }

        public A getVal() {
            return val;
        }

        @Override
        final public int tag() {
            return Tags.Pure;
        }
    }

    static final class Failed<A> extends JIO<A> {
        private Throwable t;

        public Failed(Throwable t) {
            this.t = t;
        }

        public Throwable get() {
            return t;
        }

        @Override
        final public int tag() {
            return Tags.Fail;
        }
    }

    static final class MapJIO<A, B> extends JIO<B> implements Function1<A, JIO<B>> {
        private Function1<A, B> f;
        private JIO<A> old;

        MapJIO(Function1<A, B> f, JIO<A> old) {
            this.f = f;
            this.old = old;
        }

        public Function1<A, B> getF() {
            return f;
        }

        public JIO<A> getOld() {
            return old;
        }

        @Override
        public JIO<B> apply(A a) {
            return new PureJIO<>(f.apply(a));
        }

        @Override
        final public int tag() {
            return Tags.Map;
        }
    }

    static final class DelayJIO<A> extends JIO<A> {
        private Supplier<A> f;

        public DelayJIO(Supplier<A> f) {
            this.f = f;
        }

        public Supplier<A> getF() {
            return f;
        }

        @Override
        final public int tag() {
            return Tags.Effect;
        }
    }

    @SuppressWarnings("unchecked")
    final public static <B> B unsafeRunSync(JIO<B> jio) throws Throwable {
        JIO<Object> current = (JIO<Object>) jio;
        Object ret = null;
        boolean inPure = false;
        IOLinkedArrayQueue stack = new IOLinkedArrayQueue();

        do {
            switch (current.tag()) {
                case Tags.Bind:
                    stack.push(((BindJIO<Object, Object>) current).getF());
                    current = ((BindJIO<Object, Object>) current).getOld();
                    break;
                case Tags.Pure:
                    ret = ((PureJIO<Object>) current).getVal();
                    inPure = true;
                    break;
                case Tags.Effect:
                    try {
                        ret = ((DelayJIO<Object>) current).getF().get();
                        inPure = true;
                    } catch (Throwable e) {
                        if (e instanceof VirtualMachineError) throw e;
                        else current = new Failed<>(e);
                    }
                    break;
                case Tags.Map:
                    stack.push(current);
                    current = ((MapJIO<Object, Object>) current).getOld();
                    break;
                default:
                    if (hasAttemptHandler(stack)) {
                        ret = new Left<>(((Failed<Object>) current).get());
                        inPure = true;
                    } else {
                        throw ((Failed<Object>) current).get();
                    }
                    break;
            }

            if (inPure) {
                if (!stack.isEmpty()) {
                    try {
                        current = ((Function1<Object, JIO<Object>>) stack.pop()).apply(ret);
                    } catch (Throwable t) {
                        if (t instanceof VirtualMachineError)
                            throw t;
                        else
                            current = new Failed<>(t);
                    }
                    inPure = false;
                } else return (B) ret;
            }
        } while (true);
    }

    public A unsafeRunSync() throws Throwable {
        return JIO.unsafeRunSync(this);
    }

    static boolean hasAttemptHandler(IOLinkedArrayQueue q) {
        while (!q.isEmpty()) {
            if (q.pop() == ATTEMPT)
                return true;
        }

        return false;
    }

}
