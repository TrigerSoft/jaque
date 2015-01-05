/*
 * Copyright TrigerSoft <kostat@trigersoft.com> 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.trigersoft.jaque.expression.invoke;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * Internal class. Should not be used directly.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

public final class SerializedLambda implements Serializable {
	private static final long serialVersionUID = 8025925345765570181L;
	@SuppressWarnings("javadoc")
	public final Object[] capturedArgs = null;
	@SuppressWarnings("javadoc")
	public final String implClass = null;
	@SuppressWarnings("javadoc")
	public final String implMethodName = null;
	@SuppressWarnings("javadoc")
	public final String implMethodSignature = null;

	private String instantiatedMethodType;
	private Class<?> capturingClass;
	private String functionalInterfaceClass;
	private String functionalInterfaceMethodName;
	private String functionalInterfaceMethodSignature;
	private int implMethodKind;

	@SuppressWarnings("javadoc")
	public static SerializedLambda extractLambda(Serializable lambda) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream(2000);
			try (ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
				out.writeObject(lambda);
			}

			byte[] data = byteOut.toByteArray();
			try (ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(data)) {

				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc)
						throws IOException, ClassNotFoundException {

					Class<?> resolvedClass = super.resolveClass(desc);
					if (resolvedClass == java.lang.invoke.SerializedLambda.class)
						return SerializedLambda.class;
					return resolvedClass;
				}

			}) {
				return (SerializedLambda) in.readObject();
			}

		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
}
