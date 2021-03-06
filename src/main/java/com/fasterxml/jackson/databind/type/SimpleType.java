package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public final class SimpleType extends TypeBase {
    protected final String[] _typeNames;
    protected final JavaType[] _typeParameters;

    protected SimpleType(Class<?> cls) {
        this(cls, null, null, null, null, false);
    }

    protected SimpleType(Class<?> cls, String[] typeNames, JavaType[] typeParams, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(cls, 0, valueHandler, typeHandler, asStatic);
        if (typeNames == null || typeNames.length == 0) {
            this._typeNames = null;
            this._typeParameters = null;
            return;
        }
        this._typeNames = typeNames;
        this._typeParameters = typeParams;
    }

    public static SimpleType constructUnsafe(Class<?> raw) {
        return new SimpleType(raw, null, null, null, null, false);
    }

    protected JavaType _narrow(Class<?> subclass) {
        return new SimpleType(subclass, this._typeNames, this._typeParameters, this._valueHandler, this._typeHandler, this._asStatic);
    }

    public JavaType narrowContentsBy(Class<?> cls) {
        throw new IllegalArgumentException("Internal error: SimpleType.narrowContentsBy() should never be called");
    }

    public JavaType widenContentsBy(Class<?> cls) {
        throw new IllegalArgumentException("Internal error: SimpleType.widenContentsBy() should never be called");
    }

    public SimpleType withTypeHandler(Object h) {
        return new SimpleType(this._class, this._typeNames, this._typeParameters, this._valueHandler, h, this._asStatic);
    }

    public JavaType withContentTypeHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenTypeHandler()");
    }

    public SimpleType withValueHandler(Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new SimpleType(this._class, this._typeNames, this._typeParameters, h, this._typeHandler, this._asStatic);
    }

    public SimpleType withContentValueHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenValueHandler()");
    }

    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._typeParameters != null && this._typeParameters.length > 0) {
            sb.append('<');
            boolean first = true;
            for (JavaType t : this._typeParameters) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(t.toCanonical());
            }
            sb.append('>');
        }
        return sb.toString();
    }

    public boolean isContainerType() {
        return false;
    }

    public int containedTypeCount() {
        return this._typeParameters == null ? 0 : this._typeParameters.length;
    }

    public JavaType containedType(int index) {
        if (index < 0 || this._typeParameters == null || index >= this._typeParameters.length) {
            return null;
        }
        return this._typeParameters[index];
    }

    public String containedTypeName(int index) {
        if (index < 0 || this._typeNames == null || index >= this._typeNames.length) {
            return null;
        }
        return this._typeNames[index];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("[simple type, class ").append(buildCanonicalName()).append(']');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        SimpleType other = (SimpleType) o;
        if (other._class != this._class) {
            return false;
        }
        JavaType[] p1 = this._typeParameters;
        JavaType[] p2 = other._typeParameters;
        if (p1 == null) {
            if (p2 == null || p2.length == 0) {
                return true;
            }
            return false;
        } else if (p2 == null || p1.length != p2.length) {
            return false;
        } else {
            int len = p1.length;
            for (int i = 0; i < len; i++) {
                if (!p1[i].equals(p2[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
