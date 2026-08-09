package org.lower.document.grpc.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class NameDeclensionServiceGrpc {

  private NameDeclensionServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "namedeclension.NameDeclensionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.lower.document.grpc.proto.DeclineFullNameRequest,
      org.lower.document.grpc.proto.DeclineFullNameResponse> getDeclineFullNameMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeclineFullName",
      requestType = org.lower.document.grpc.proto.DeclineFullNameRequest.class,
      responseType = org.lower.document.grpc.proto.DeclineFullNameResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.lower.document.grpc.proto.DeclineFullNameRequest,
      org.lower.document.grpc.proto.DeclineFullNameResponse> getDeclineFullNameMethod() {
    io.grpc.MethodDescriptor<org.lower.document.grpc.proto.DeclineFullNameRequest, org.lower.document.grpc.proto.DeclineFullNameResponse> getDeclineFullNameMethod;
    if ((getDeclineFullNameMethod = NameDeclensionServiceGrpc.getDeclineFullNameMethod) == null) {
      synchronized (NameDeclensionServiceGrpc.class) {
        if ((getDeclineFullNameMethod = NameDeclensionServiceGrpc.getDeclineFullNameMethod) == null) {
          NameDeclensionServiceGrpc.getDeclineFullNameMethod = getDeclineFullNameMethod =
              io.grpc.MethodDescriptor.<org.lower.document.grpc.proto.DeclineFullNameRequest, org.lower.document.grpc.proto.DeclineFullNameResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeclineFullName"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.lower.document.grpc.proto.DeclineFullNameRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.lower.document.grpc.proto.DeclineFullNameResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameDeclensionServiceMethodDescriptorSupplier("DeclineFullName"))
              .build();
        }
      }
    }
    return getDeclineFullNameMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NameDeclensionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceStub>() {
        @java.lang.Override
        public NameDeclensionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameDeclensionServiceStub(channel, callOptions);
        }
      };
    return NameDeclensionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static NameDeclensionServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceBlockingV2Stub>() {
        @java.lang.Override
        public NameDeclensionServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameDeclensionServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return NameDeclensionServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NameDeclensionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceBlockingStub>() {
        @java.lang.Override
        public NameDeclensionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameDeclensionServiceBlockingStub(channel, callOptions);
        }
      };
    return NameDeclensionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NameDeclensionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameDeclensionServiceFutureStub>() {
        @java.lang.Override
        public NameDeclensionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameDeclensionServiceFutureStub(channel, callOptions);
        }
      };
    return NameDeclensionServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void declineFullName(org.lower.document.grpc.proto.DeclineFullNameRequest request,
        io.grpc.stub.StreamObserver<org.lower.document.grpc.proto.DeclineFullNameResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeclineFullNameMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service NameDeclensionService.
   */
  public static abstract class NameDeclensionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return NameDeclensionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service NameDeclensionService.
   */
  public static final class NameDeclensionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<NameDeclensionServiceStub> {
    private NameDeclensionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameDeclensionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameDeclensionServiceStub(channel, callOptions);
    }

    /**
     */
    public void declineFullName(org.lower.document.grpc.proto.DeclineFullNameRequest request,
        io.grpc.stub.StreamObserver<org.lower.document.grpc.proto.DeclineFullNameResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeclineFullNameMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service NameDeclensionService.
   */
  public static final class NameDeclensionServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<NameDeclensionServiceBlockingV2Stub> {
    private NameDeclensionServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameDeclensionServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameDeclensionServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public org.lower.document.grpc.proto.DeclineFullNameResponse declineFullName(org.lower.document.grpc.proto.DeclineFullNameRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDeclineFullNameMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service NameDeclensionService.
   */
  public static final class NameDeclensionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<NameDeclensionServiceBlockingStub> {
    private NameDeclensionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameDeclensionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameDeclensionServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.lower.document.grpc.proto.DeclineFullNameResponse declineFullName(org.lower.document.grpc.proto.DeclineFullNameRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeclineFullNameMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service NameDeclensionService.
   */
  public static final class NameDeclensionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<NameDeclensionServiceFutureStub> {
    private NameDeclensionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameDeclensionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameDeclensionServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.lower.document.grpc.proto.DeclineFullNameResponse> declineFullName(
        org.lower.document.grpc.proto.DeclineFullNameRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeclineFullNameMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DECLINE_FULL_NAME = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DECLINE_FULL_NAME:
          serviceImpl.declineFullName((org.lower.document.grpc.proto.DeclineFullNameRequest) request,
              (io.grpc.stub.StreamObserver<org.lower.document.grpc.proto.DeclineFullNameResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getDeclineFullNameMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.lower.document.grpc.proto.DeclineFullNameRequest,
              org.lower.document.grpc.proto.DeclineFullNameResponse>(
                service, METHODID_DECLINE_FULL_NAME)))
        .build();
  }

  private static abstract class NameDeclensionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NameDeclensionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.lower.document.grpc.proto.NameDeclensionProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NameDeclensionService");
    }
  }

  private static final class NameDeclensionServiceFileDescriptorSupplier
      extends NameDeclensionServiceBaseDescriptorSupplier {
    NameDeclensionServiceFileDescriptorSupplier() {}
  }

  private static final class NameDeclensionServiceMethodDescriptorSupplier
      extends NameDeclensionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    NameDeclensionServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NameDeclensionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NameDeclensionServiceFileDescriptorSupplier())
              .addMethod(getDeclineFullNameMethod())
              .build();
        }
      }
    }
    return result;
  }
}
